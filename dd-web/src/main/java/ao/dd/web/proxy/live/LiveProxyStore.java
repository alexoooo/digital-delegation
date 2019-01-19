package ao.dd.web.proxy.live;

import ao.dd.web.proxy.model.AnonProxy;
import ao.dd.web.proxy.select.Selector;
import ao.dd.web.proxy.store.ProxyStore;
import ao.dd.web.proxy.store.ProxyStoreImpl;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Date: Oct 12, 2008
 * Time: 2:57:58 PM
 */
@Singleton
public class LiveProxyStore implements ProxyStore
{
    //--------------------------------------------------------------------
    private final static Logger LOG =
            Logger.getLogger(LiveProxyStore.class);

    private final static long REFRESH_PAUSE = 60 * 60 * 1000;


    //--------------------------------------------------------------------
    private final ProxyStoreImpl delegate;
    private final CountDownLatch populated;


    //--------------------------------------------------------------------
    @Inject public LiveProxyStore(
            final ProxyStoreImpl deleget,
            final ProxyProvider  proxyProvider)
    {
        this.delegate = deleget;
        populated = new CountDownLatch(
                deleget.isEmpty() ? 0 : 1 );

        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(
                new Runnable() {
                    public void run() {
                        try
                        {
                            doRetrieveProxies( proxyProvider );
                        }
                        catch (Throwable t)
                        {
                            LOG.error("could not retrieve proxies", t);
                        }
                        finally
                        {
                            populated.countDown();
                        }
                    }
                },
                0,
                REFRESH_PAUSE, TimeUnit.MILLISECONDS);
    }


    //--------------------------------------------------------------------
    public void add(Collection<AnonProxy> anonProxies)
    {
        delegate.add( anonProxies );
    }

    public boolean process(Selector s, Processor w)
    {
        try
        {
            populated.await();
        }
        catch (InterruptedException ignored)
        {
            Thread.currentThread().interrupt();
        }

        return delegate.process(s, w);
    }

    public boolean isEmpty()
    {
        return delegate.isEmpty();
    }


    //--------------------------------------------------------------------
    private void doRetrieveProxies(ProxyProvider provider)
    {
        delegate.add( provider.proxies() );
    }
}
