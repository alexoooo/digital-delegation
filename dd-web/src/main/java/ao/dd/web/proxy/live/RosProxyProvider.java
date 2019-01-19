package ao.dd.web.proxy.live;

import ao.dd.common.WebUtils;
import ao.dd.web.alexo.UserAgent;
import ao.dd.web.alexo.UserAgentImpl;
import ao.dd.web.proxy.model.AnonProxy;
import ao.util.time.Sched;
import com.google.inject.Singleton;
import org.apache.log4j.Logger;

import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class RosProxyProvider implements ProxyProvider
{
    //--------------------------------------------------------------------
    private final static Logger LOG =
            Logger.getLogger(RosProxyProvider.class);


    //--------------------------------------------------------------------
    private static final long COOKIE_LOAD_PAUSE = 2000;
    private static final long COURTASY_PAUSE    = 5000;


    @SuppressWarnings("serial")
	@Singleton
    public static class MultiRosProxyProvider implements ProxyProvider
    {
        private final UserAgent              agent     =
                                                new UserAgentImpl();
        private final List<RosProxyProvider> instances =
                new ArrayList<RosProxyProvider>(){{
                    for (SortOrder order : SortOrder.values())
                    {
                        add(new RosProxyProvider(
                                agent, order, Type.HTTP));
                        add(new RosProxyProvider(
                                agent, order, Type.SOCKS));
                    }
                }};
        public Collection<AnonProxy> proxies()
        {
            preloadCookies( agent );

            Set<AnonProxy> proxies = new HashSet<AnonProxy>();
            for (int i = 0; i < instances.size(); i++)
            {
                RosProxyProvider instance = instances.get(i);

                if (i != 0)
                {
                    Sched.sleep(COURTASY_PAUSE);
                }
                
                proxies.addAll( instance.doGetPoxies() );
            }

            LOG.debug("retrieved " + proxies.size() + " proxies");
            return proxies;
        }
    }


    //--------------------------------------------------------------------
    private static final URL PROXY_LIST = WebUtils.asUrl(
            "http://tools.rosinstrument.com/cgi-bin/fp.pl/showlines");

    private static final Pattern TEXT_HIDER = Pattern.compile(
            ".*String\\.fromCharCode\\" +
                "(s\\.charCodeAt\\(i\\)\\^(\\d+)\\);.*",
            Pattern.DOTALL);

    private static final Pattern PROXY_ROW = Pattern.compile(
            "hideTxt\\('(.*?)'\\).*?<td><b>(.*?)</b></td>",
            Pattern.DOTALL);


    //--------------------------------------------------------------------
    private final UserAgent  AGENT;
    private final SortOrder  ORDER;
    private final Proxy.Type TYPE;


    //--------------------------------------------------------------------
    public RosProxyProvider()
    {
        this( new UserAgentImpl(),
              SortOrder.CHECK_DATE,
              Type.HTTP );
    }
    private RosProxyProvider(UserAgent  agent,
                             SortOrder  order,
                             Proxy.Type type)
    {
        assert type != Type.DIRECT;

        AGENT = agent;
        TYPE  = type;
        ORDER = order;
    }


    //--------------------------------------------------------------------
    public synchronized List<AnonProxy> proxies()
    {
        preloadCookies(AGENT);
        return doGetPoxies();
    }
    
    @SuppressWarnings("serial")
	private synchronized List<AnonProxy> doGetPoxies()
    {
        LOG.trace("retrieving proxies" + toString());

        String proxyPage =
                AGENT.load(PROXY_LIST, new HashMap<String, String>(){{
                    put("lines",  "20");

                    put("sortor", String.valueOf(
                                    ORDER.ordinal()));

                    put("filter", TYPE.toString());
                }}, false).toString();

        Matcher textHider = TEXT_HIDER.matcher(proxyPage);
        if (! textHider.matches()) throw new Error();
        int hideBy = Integer.parseInt(textHider.group(1));

        List<AnonProxy> proxies      = new ArrayList<AnonProxy>();
        Matcher         proxyMatcher = PROXY_ROW.matcher(proxyPage);
        while (proxyMatcher.find())
        {
            if (proxyMatcher.group(2).contains("&lt;")) continue;

            AnonProxy proxy = new AnonProxy(
                    decode(proxyMatcher.group(1), hideBy));
            proxies.add( proxy );
        }
        return proxies;
    }

    private static void preloadCookies(UserAgent agent)
    {
        agent.load( PROXY_LIST );
        Sched.sleep( COOKIE_LOAD_PAUSE );
    }


    //--------------------------------------------------------------------
    private static String decode(String proxyAddress,
                                 int    hideBy)
    {
        String text   = WebUtils.unEscapeUrl(proxyAddress);

        StringBuilder out = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++)
        {
            char x = text.charAt(i);
            char c = (char)( ((int) x) ^ hideBy );
            out.append( c );
        }
        return out.toString();
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return "RosProxyProvider{" +
               "ORDER=" + ORDER +
               ", TYPE=" + TYPE +
               '}';
    }


    //--------------------------------------------------------------------
    private static enum SortOrder
    {
        UNSORTED, HOST_NAME, PORT, SPEED, CHECK_DATE
    }
}
