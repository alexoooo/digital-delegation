package ao.dd.web.proxy.live;

import java.util.Collection;

import ao.dd.web.proxy.live.RosProxyProvider.MultiRosProxyProvider;
import ao.dd.web.proxy.model.AnonProxy;

import com.google.inject.ImplementedBy;

/**
 *
 */
@ImplementedBy( MultiRosProxyProvider.class )
public interface ProxyProvider
{
    public Collection<AnonProxy> proxies();
}
