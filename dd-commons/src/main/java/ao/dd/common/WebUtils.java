package ao.dd.common;

import ao.util.math.rand.Rand;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Utilities to help with Web Programming.
 */
public class WebUtils
{
    //-------------------------------------------------------------------------
    private WebUtils() {}


    //-------------------------------------------------------------------------
    private static final List<String> USER_AGENTS = Arrays.asList(
            "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; " +
                    "rv:1.8.1.14) Gecko/20080404 Firefox/2.0.0.14",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; nl; rv:1.8)",
            "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.0.4)" +
                    " Gecko/20060612 Firefox/1.5.0.4 Flock/0.7.0.17.1",
            "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9b5)" +
                    " Gecko/2008050509 Firefox/3.0b5",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US;" +
                    " rv:1.7.13) Gecko/20060410 Firefox/1.0.8",
            "Mozilla/5.0 (X11; U; FreeBSD i386; en-US; rv:1.7.12)" +
                    " Gecko/20051105 Galeon/1.3.21",
            "Mozilla/5.0 (compatible; Konqueror/4.0; Microsoft Windows)" +
                    " KHTML/4.0.80 (like Gecko)",
            "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; WOW64;" +
                    " SLCC1; .NET CLR 2.0.50727; .NET CLR 3.0.04506;" +
                    " Media Center PC 5.0; .NET CLR 1.1.4322)",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1;" +
                    " .NET CLR 2.0.50727; Zune 2.0)",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; " +
                    "NeosBrowser; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
            "Mozilla/4.0 (compatible; MSIE 5.5; Windows 98)",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US;" +
                    " rv:1.8.1.8pre) Gecko/20071019 Firefox/2.0.0.8" +
                    " Navigator/9.0.0.1",
            "Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.7.5)" +
                    " Gecko/20050519 Netscape/8.0.1",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.2)" +
                    " Gecko/20040804 Netscape/7.2 (ax)",
            "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.0.4)" +
                    " Gecko/20061019 pango-text",
            "Opera/9.20 (Macintosh; Intel Mac OS X; U; en)",
            "Opera/9.00 (Windows NT 5.1; U; en)",
            "Opera/8.00 (Windows NT 5.1; U; en)",
            "Mozilla/5.0 (iPod; U; CPU like Mac OS X; en)" +
                    " AppleWebKit/420.1 (KHTML, like Gecko)" +
                    " Version/3.0 Mobile/3A101a Safari/419.3",
            "Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10_5_2; en-us)" +
                    " AppleWebKit/525.13 (KHTML, like Gecko)" +
                    " Version/3.1 Safari/525.13",
            "Mozilla/5.0 (iPod; U; CPU like Mac OS X; en)" +
                    " AppleWebKit/420.1 (KHTML, like Gecko)" +
                    " Version/3.0 Mobile/3A100a Safari/419.3",
            "Mozilla/4.0 (compatible; Mozilla/4.0; Mozilla/5.0;" +
                    " Mozilla/6.0; Safari/431.7; Macintosh; U;" +
                    " PPC Mac OS X 10.6 Leopard;" +
                    " AppleWebKit/421.9 (KHTML, like Gecko) )",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; ru)" +
                    " AppleWebKit/522.11.3 (KHTML, like Gecko)" +
                    " Version/3.0 Safari/522.11.3",
            "Mozilla/5.0 (Macintosh; U; Intel Mac OS X; en)" +
                    " AppleWebKit/419.3 (KHTML, like Gecko) Safari/419.3",
            "Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.7.6)" +
                    " Gecko/20050225 Firefox/1.0.1"
    );

    public static String randomUserAgent()
    {
        return Rand.fromList( USER_AGENTS );
    }


    //--------------------------------------------------------------------
    public static String unEscapeHtml(String htmlEncoded)
    {
        return htmlEncoded
                .replaceAll("&lt;"  , "<")
                .replaceAll("&gt;"  , ">")
                .replaceAll("&amp;" , "&")
                .replaceAll("&quot;", "\"");
    }


    //-------------------------------------------------------------------------
    public static URL asUrl(String url)
    {
        try
        {
            return new URL( url );
        }
        catch (MalformedURLException e)
        {
            throw new Error(e);
        }
    }

    public static URL asUrl(URL context, String url)
    {
        try
        {
            return new URL( context, url );
        }
        catch (MalformedURLException e)
        {
            throw new Error(e);
        }
    }


    //-------------------------------------------------------------------------
    public static String escapeUrl(String urlText)
    {
        try
        {
            return URLEncoder.encode(urlText, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new Error(e);
        }
    }


    //-------------------------------------------------------------------------
    public static String unEscapeUrl(String urlText)
    {
        try
        {
            return URLDecoder.decode(urlText, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new Error( e );
        }
    }


    //-------------------------------------------------------------------------
    public static String urlEncode(Map<String, String> pairs)
    {
        StringBuilder s = new StringBuilder();
        for (Map.Entry<String, String> post : pairs.entrySet())
        {
            s.append('&')
             .append( escapeUrl(post.getKey()  ) )
             .append('=')
             .append( escapeUrl(post.getValue()) );
        }
        return s.substring(1);
    }


    //-------------------------------------------------------------------------
    /**
     * @param hostnameOrIp to look up
     * @return ip address, or null if not available
     */
    public static String nsLookup(String hostnameOrIp)
    {
        if (hostnameOrIp.contains(".") ||
                hostnameOrIp.contains(":"))
        {
            return hostnameOrIp;
        }

        try
        {
            InetAddress remoteHost =
                    InetAddress.getByName( hostnameOrIp );
            return remoteHost.getHostAddress();
        }
        catch (UnknownHostException ignored)
        {
            return null;
        }
    }
}
