package ao.dd.web.alexo;

import java.net.URL;
import java.util.Map;

import ao.dd.web.apache.Session;

/**
 *
 */
public class SessionAdapter implements Session
{
    //--------------------------------------------------------------------
    private UserAgent agent = new UserAgentImpl();


    //--------------------------------------------------------------------
    public String load(URL address)
    {
        return agent.load(address).toString();
    }

    public String load(URL address, Map<String, String> data, boolean isPost)
    {
        return agent.load(address, data, isPost).toString();
    }

    public String load(URL                 address,
                       URL                 referer,
                       Map<String, String> data,
                       boolean             isPost)
    {
        return agent.load(address, referer, data, isPost).toString();
    }
}
