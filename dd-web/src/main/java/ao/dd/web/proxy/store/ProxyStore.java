package ao.dd.web.proxy.store;

import java.util.Collection;

import ao.dd.web.proxy.model.AnonProxy;
import ao.dd.web.proxy.select.Selector;

import com.google.inject.ImplementedBy;

/**
 * Date: Oct 12, 2008
 * Time: 4:20:28 PM
 */
@ImplementedBy( ProxyStoreImpl.class )
public interface ProxyStore
{
    //--------------------------------------------------------------------
    public void add(Collection<AnonProxy> anonProxies);

    public boolean process(Selector s, Processor w);

    public boolean isEmpty();


    //--------------------------------------------------------------------
    public static interface Processor
    {
        public boolean process(AnonProxy proxy);
    }
}
