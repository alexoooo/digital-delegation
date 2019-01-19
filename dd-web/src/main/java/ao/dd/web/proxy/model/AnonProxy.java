package ao.dd.web.proxy.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ao.dd.web.apache.Session;
import ao.dd.web.apache.SessionManager;

/**
 *
 */
public class AnonProxy
{
    //--------------------------------------------------------------------
    private static final Pattern PROXY = Pattern.compile(
            "(.*?)://(.*):(\\d+)");


    //--------------------------------------------------------------------
    private final String protocol;
    private final String host;
    private final int    port;


    //--------------------------------------------------------------------
    public AnonProxy(String address)
    {
        Matcher m = PROXY.matcher( address );
        if (! m.matches()) throw new Error( address );

        protocol = m.group(1);
        host     = m.group(2);
        port     = Integer.parseInt(
                    m.group(3));
    }


    //--------------------------------------------------------------------
    public Session open(SessionManager in)
    {
        return in.open(protocol, host, port);
    }


    //--------------------------------------------------------------------
    public boolean isSocks()
    {
        return protocol.equalsIgnoreCase("socks");
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return protocol + "://" +
               host     + ":"   +
               port;
    }


    //--------------------------------------------------------------------
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnonProxy anonProxy = (AnonProxy) o;
        return port == anonProxy.port &&
               host.equals(anonProxy.host) &&
               protocol.equals(anonProxy.protocol);
    }

    public int hashCode()
    {
        int result;
        result = protocol.hashCode();
        result = 31 * result + host.hashCode();
        result = 31 * result + port;
        return result;
    }
}
