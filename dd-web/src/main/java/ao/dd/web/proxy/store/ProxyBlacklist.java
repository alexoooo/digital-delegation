package ao.dd.web.proxy.store;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import ao.dd.web.proxy.model.AnonProxy;

import com.google.inject.Singleton;

/**
 * Date: Oct 17, 2008
 * Time: 2:23:42 AM
 */
@Singleton
public class ProxyBlacklist implements Blacklist<AnonProxy>
{
    //--------------------------------------------------------------------
    private final static Logger LOG =
            Logger.getLogger(ProxyBlacklist.class);


    //--------------------------------------------------------------------
    private static final File BLACKLIST_FILE =
            new File("proxy/blacklist.csv");


    //--------------------------------------------------------------------
    private final Set<AnonProxy> BLACKLIST = load();


    //--------------------------------------------------------------------
    public void blacklist(AnonProxy entity)
    {
        if (BLACKLIST.add( entity ))
        {
            store( BLACKLIST );
        }
    }


    //--------------------------------------------------------------------
    public boolean isBlacklisted(AnonProxy entity)
    {
        return BLACKLIST.contains( entity );
    }


    //--------------------------------------------------------------------
    private static void store(Set<AnonProxy> blacklist)
    {
        try
        {
            doStore(blacklist);
        }
        catch (IOException e)
        {
            LOG.error("can't store", e);
        }
    }
    private static void doStore(Set<AnonProxy> blacklist)
            throws IOException
    {
        PrintStream os = new PrintStream(BLACKLIST_FILE);
        for (AnonProxy proxy : blacklist)
        {
            os.println( proxy.toString() );
        }
        os.close();
    }


    //--------------------------------------------------------------------
    private Set<AnonProxy> load()
    {
        try
        {
            return doLoad();
        }
        catch (IOException e)
        {
            LOG.error("can't load", e);
            return null;
        }
    }

    private Set<AnonProxy> doLoad()
            throws IOException
    {
        Set<AnonProxy> blacklist = new LinkedHashSet<AnonProxy>();
        if (! BLACKLIST_FILE.exists()) return blacklist;

        LOG.debug("loading from CSV");
        BufferedReader in =
                new BufferedReader(
                        new FileReader(BLACKLIST_FILE));
        for (String line  = in.readLine();
                    line != null;
                    line  = in.readLine())
        {
            blacklist.add( new AnonProxy(line) );
        }
        in.close();
        return blacklist;
    }
}
