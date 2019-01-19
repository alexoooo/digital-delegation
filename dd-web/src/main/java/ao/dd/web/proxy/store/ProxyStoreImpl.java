package ao.dd.web.proxy.store;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import ao.dd.web.proxy.model.AnonProxy;
import ao.dd.web.proxy.model.Status;
import ao.dd.web.proxy.select.Selector;
import ao.util.time.Sched;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Date: Oct 10, 2008
 * Time: 12:36:53 PM
 *
 * Persists information about proxies
 */
@Singleton
public class ProxyStoreImpl implements ProxyStore
{
    //--------------------------------------------------------------------
    private final static Logger LOG =
            Logger.getLogger(ProxyStoreImpl.class);


    //--------------------------------------------------------------------
    private static final String POOL_SEPARATOR = "\t";
    private static final String PROXY_DIR      = "proxy";
    private static final String PROXY_FILE     = "pool.csv";

    
    //--------------------------------------------------------------------
    private final Blacklist<AnonProxy>   BLACKLIST;
    private final Map<AnonProxy, Status> store  =
                            new LinkedHashMap<AnonProxy, Status>();
    private final Set<AnonProxy>         locked =
                            new HashSet<AnonProxy>();

    private final Timer   writer    = new Timer("proxy persister", false);
    private       boolean isWriting = false;
    private       int     numWrites = 0;
    private       long    lastWrite = 0;


    //--------------------------------------------------------------------
    @Inject public ProxyStoreImpl(
            Blacklist<AnonProxy> blacklist)
    {
        BLACKLIST = blacklist;
        loadProxies();
    }


    //--------------------------------------------------------------------
    public synchronized void add(Collection<AnonProxy> anonProxies)
    {
        boolean added = false;
        for (AnonProxy p : anonProxies)
        {
            if (! store.containsKey( p ) &&
                ! BLACKLIST.isBlacklisted( p ))
            {
                store.put( p, new Status() );
                added = true;
            }
        }
        if (added)
        {
            storeProxies();
        }
    }


    //--------------------------------------------------------------------
    public boolean process(Selector  selector,
                           Processor processor)
    {
        AnonProxy selected = selectAndLock(selector);
        LOG.debug("selected " + selected + " :: " + store.get(selected));

        long    before  = System.currentTimeMillis();
        boolean success = processor.process( selected );
        long    runtime = System.currentTimeMillis() - before;

        synchronized( ProxyStoreImpl.class )
        {
            updateAndUnlock(selected, success, runtime);
        }
        storeProxies();
        return success;
    }

    //--------------------------------------------------------------------
    private void updateAndUnlock(
            AnonProxy selected, boolean success, long runtime)
    {
        Status status  = store.get( selected );
        Status updated = status.update(success, runtime);

        if (updated.hasGoodRoi())
        {
            store.put(selected, updated);

            LOG.debug(
                    (updated.passedMore(status)
                     ? "passed" : "failed") +
                    " to " + updated);
        }
        else
        {
            store.remove(selected);
            BLACKLIST.blacklist(selected);
            LOG.debug("blacklisted " + updated);
        }

        locked.remove( selected );
    }

    //--------------------------------------------------------------------
    private AnonProxy selectAndLock(Selector s)
    {
        AnonProxy selected;
        while ((selected = doSelectAndLock(s)) == null)
        {
            Sched.sleep(250);
        }
        return selected;
    }
    private synchronized AnonProxy doSelectAndLock(Selector s)
    {
        LOG.trace("selecting one of " + store.size() + " proxies");

        Map<AnonProxy, Status> eligible =
                new HashMap<AnonProxy, Status>();
        for (Map.Entry<AnonProxy, Status> e : store.entrySet())
        {
            if (e.getValue().isEligible() &&
                !BLACKLIST.isBlacklisted( e.getKey() ) &&
                !locked.contains( e.getKey() ))
            {
                eligible.put( e.getKey(), e.getValue() );
            }
        }
        if (eligible.isEmpty())
        {
            LOG.trace("no eligible proxies found");
            return null;
        }

        AnonProxy selected = s.select( eligible );
        locked.add( selected );
        return selected;
    }


    //--------------------------------------------------------------------
    public synchronized boolean isEmpty()
    {
        return store.isEmpty();
    }


    //--------------------------------------------------------------------
    private synchronized void loadProxies()
    {
        try
        {
            doLoadProxies();
        }
        catch (IOException e)
        {
            LOG.fatal("Could not load proxies", e);
        }
    }

    private void doLoadProxies() throws IOException
    {
        File poolFile = openProxyPool();
        if (! poolFile.exists()) return;

        BufferedReader in =
                new BufferedReader(
                        new FileReader(poolFile));

        for (String line  = in.readLine();
                    line != null;
                    line  = in.readLine())
        {
            AnonProxy proxy = proxyFromString(line);
            Status stats = statsFromString(line);

            if (! stats.hasGoodRoi())
            {
                BLACKLIST.blacklist( proxy );
            }
            else if (! store.containsKey( proxy ) &&
                     ! BLACKLIST.isBlacklisted( proxy ))
            {
                store.put(proxy, stats);
//                store.put(proxy, new Status());
            }
        }

        in.close();
    }


    //--------------------------------------------------------------------
    private synchronized void storeProxies()
    {
        if (isWriting) return;
        isWriting = true;

        final long sinceLastWrite =
                (System.currentTimeMillis() - lastWrite);
        final long waitTime       =
                Math.max(0, Math.min(numWrites, 5 * 60) * 1000
                            - sinceLastWrite);
        writer.schedule(new TimerTask() {
            @Override public void run() {
                try
                {
                    LOG.debug("persisting: " +
                             store.size() + " " +
                             numWrites + " " +
                             waitTime);
                    doStoreProxies();
                }
                catch (Throwable e)
                {
                    LOG.error("Could not persist proxies", e);
                }
                finally
                {
                    numWrites++;
                    isWriting = false;
                    lastWrite = System.currentTimeMillis();
                }
            }
        }, waitTime);
    }

    private synchronized void doStoreProxies()
            throws FileNotFoundException
    {
        File        poolFile = openProxyPool();
        PrintWriter out      = new PrintWriter(poolFile);

        for (Map.Entry<AnonProxy, Status> proxy :
                store.entrySet())
        {
            out.println(toString(
                    proxy.getKey(), proxy.getValue()));
        }

        out.close();
    }


    //--------------------------------------------------------------------
    private static String toString(AnonProxy proxy, Status stats)
    {
        return proxy.toString() + POOL_SEPARATOR +
               stats.toCsv();
    }
    private static AnonProxy proxyFromString(String proxyStatsString)
    {
        return new AnonProxy(
                proxyStatsString.substring(0,
                        proxyStatsString.indexOf(POOL_SEPARATOR)));
    }
    private static Status statsFromString(String proxyStatsString)
    {
        return new Status(
                proxyStatsString.substring(
                        proxyStatsString.indexOf(POOL_SEPARATOR) +
                            POOL_SEPARATOR.length()));
    }

    private File openProxyPool()
    {
        File dir = new File(PROXY_DIR);
        if (dir.mkdir())
        {
            LOG.debug("Created " + PROXY_DIR + " folder.");
        }

        return new File(dir, PROXY_FILE);
    }
}
