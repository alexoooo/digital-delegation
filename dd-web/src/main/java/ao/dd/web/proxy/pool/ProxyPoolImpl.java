package ao.dd.web.proxy.pool;

import java.io.InterruptedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import ao.dd.web.RequestError;
import ao.dd.web.apache.ApacheSessionManager;
import ao.dd.web.apache.Session;
import ao.dd.web.apache.SessionManager;
import ao.dd.web.proxy.live.LiveProxyStore;
import ao.dd.web.proxy.model.AnonProxy;
import ao.dd.web.proxy.select.UcbSelector;
import ao.dd.web.proxy.store.ProxyStore;
import ao.dd.web.proxy.store.ProxyStore.Processor;
import ao.util.time.Sched;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;

/**
 *
 */
public class ProxyPoolImpl implements ProxyPool
{
    //--------------------------------------------------------------------
    private final static Logger LOG =
            Logger.getLogger(ProxyPoolImpl.class);

    public static ProxyPool newInstance()
    {
        return Guice.createInjector(new AbstractModule() {
                    @Override protected void configure() {

                    }
        }).getInstance(ProxyPoolImpl.class);
    }


    //--------------------------------------------------------------------
    public static void main(String args[]) throws InterruptedException
    {
        ProxyPool proxyPool = newInstance();
        for (int i = 0; i < 1000; i++) {
            proxyPool.submitAndWait(Arrays.<Worker>asList(new Worker() {
                public void work(Session with) throws Throwable {
                    with.load(new URL("http://www.google.com/"));
                    System.out.println("DONE google!!!");
                }}, new Worker() {
                public void work(Session with) throws Throwable {
                    with.load(new URL("http://www.perl.com/"));
                    System.out.println("DONE perl!!!");
                }}, new Worker() {
                public void work(Session with) throws Throwable {
                    with.load(new URL("http://www.slashdot.org/"));
                    System.out.println("DONE slashdot!!!");
                }
            }));
            Sched.sleep(60 * 1000);
        }

//        proxyPool = null;
//        System.gc();
    }


    //--------------------------------------------------------------------
    private static final int CONCURRANCY = 2;
    private static final int RETRY_COUNT = 1000;


    //--------------------------------------------------------------------
    private final ProxyStore      STORE;
    private final SessionManager  mgr  = new ApacheSessionManager();
    private final ExecutorService exec =
                    Executors.newFixedThreadPool(CONCURRANCY);


    //--------------------------------------------------------------------
    @Inject public ProxyPoolImpl(
                LiveProxyStore store)
    {
        STORE = store;
    }


    //--------------------------------------------------------------------
    /**
     * Submits a task and waits for its completion,
     *  the task will be restarted as long as it takes to
     *  complete the task without timing out.
     *
     * @param forWorker the task to complete
     */
    public void submitAndWait(Worker forWorker)
    {
        int i = 0;
        while (i++ < RETRY_COUNT)
        {
            boolean success = doSubmitAndWait(forWorker);
            if (success) return;

            LOG.trace("unsuccessful doSubmitAndWait");
            Sched.sleep(i * 100);
        }
        throw new RequestError("Retry count exhausted.");
    }
    private boolean doSubmitAndWait(
            final Worker forWorker)
    {
        LOG.trace("processing store");
        return STORE.process(new UcbSelector(),
                             new Processor() {
            public boolean process(AnonProxy proxy) {
                LOG.trace("processing " + proxy);
                return doSubmitAndWait(
                        forWorker,
//                        proxy.open(new ApacheSessionManager()));
                        proxy.open(mgr));
            }
        });
    }
    private boolean doSubmitAndWait(
            Worker worker, Session session)
    {
        try
        {
            return workOnSession(worker, session);
        }
        catch (Throwable throwable)
        {
            if (!(throwable.getCause() instanceof
                    InterruptedIOException))
            {
                try
                {
                    return workOnSession(worker, session);
                }
                catch (Throwable throwableB)
                {
                    LOG.debug("workOnSession failed: " +
                                throwableB.getMessage());

//                    throwableB.printStackTrace();
//                    System.out.println(
//                            throwableB.getMessage());
                }
            }
            else
            {
                LOG.debug("skipping " + throwable.getCause());
            }
        }

        return false;
    }

    private boolean workOnSession(
            Worker worker, Session session)
            throws Throwable
    {
        worker.work( session );
        return true;
    }


    //--------------------------------------------------------------------
    public void submitAndWait(Worker... workers)
    {
        submitAndWait( Arrays.asList(workers) );
    }

    @SuppressWarnings("unchecked")
    public void submitAndWait(List<Worker> workers)
    {
        final Collection<Callable<Void>> tasks =
                new ArrayList<Callable<Void>>();

        for (final Worker worker : workers)
        {
            tasks.add(new Callable() {
                public Void call() throws Exception {
                    submitAndWait(worker);
                    return null;
                }
            });
        }

        try
        {
            LOG.trace("submitting " + tasks.size() + " tasks");
            exec.invokeAll( tasks );
        }
        catch (Exception e)
        {
            LOG.error("Could not invoke all workers", e);
//            e.printStackTrace();
        }
    }
}
