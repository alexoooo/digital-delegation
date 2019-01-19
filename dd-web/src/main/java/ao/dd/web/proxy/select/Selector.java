package ao.dd.web.proxy.select;

import java.util.Map;

import ao.dd.web.proxy.model.AnonProxy;
import ao.dd.web.proxy.model.Status;

/**
 * Date: Dec 21, 2008
 * Time: 5:57:00 PM
 */
public interface Selector
{
    public AnonProxy select(
            Map<AnonProxy, Status> eligibleProxies);
}
