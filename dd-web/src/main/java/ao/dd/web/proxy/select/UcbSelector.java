package ao.dd.web.proxy.select;

import java.util.Map;

import ao.dd.web.proxy.model.AnonProxy;
import ao.dd.web.proxy.model.Status;

/**
 * Date: Dec 21, 2008
 * Time: 5:54:54 PM
 */
public class UcbSelector implements Selector
{
    public AnonProxy select(Map<AnonProxy, Status> eligibleProxies)
    {
        int totalTrials = 0;
        for (Status status : eligibleProxies.values())
        {
            totalTrials += status.trialCount();
        }

        double    maxUcb      = 0;
        AnonProxy maxUcbProxy = null;
        for (Map.Entry<AnonProxy, Status> e :
                eligibleProxies.entrySet())
        {
            double ucb = e.getValue().ubc1(totalTrials);
            if (ucb > maxUcb)
            {
                maxUcb      = ucb;
                maxUcbProxy = e.getKey();
            }
        }
        return maxUcbProxy;
    }
}
