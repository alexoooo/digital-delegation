package ao.dd.web.proxy.pool;

import java.util.List;

import ao.dd.web.apache.Session;

/**
 * Date: Dec 21, 2008
 * Time: 6:01:09 PM
 */
public interface ProxyPool
{
    //--------------------------------------------------------------------
    void submitAndWait(Worker forWorker);

    void submitAndWait(Worker... workers);

    void submitAndWait(List<Worker> workers);


    //--------------------------------------------------------------------
    public static interface Worker
    {
        public void work(Session with) throws Throwable;
    }
}
