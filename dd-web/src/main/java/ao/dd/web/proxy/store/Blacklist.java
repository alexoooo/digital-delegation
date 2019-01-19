package ao.dd.web.proxy.store;

import com.google.inject.ImplementedBy;

/**
 * Date: Oct 17, 2008
 * Time: 2:21:45 AM
 */
@ImplementedBy(ProxyBlacklist.class)
public interface Blacklist<T>
{
    //--------------------------------------------------------------------
    public void blacklist(T entity);

    public boolean isBlacklisted(T entity);
}
