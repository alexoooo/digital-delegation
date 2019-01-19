package ao.dd.web.alexo;

import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * CODE COPIED FROM http://www.hccp.org/java-net-cookie-how-to.html
 * Modified by Alex Ostrovsky 2005-12
 *
 * <p/>
 * <p/>
 * CookieManager is a simple utilty for handling cookies when working
 * with <code>java.net.URL</code> and <code>java.net.URLConnection</code>
 * objects.
 * <p/>
 * <code>
 * Cookiemanager cm = new CookieManager();
 * URL url = new URL("http://www.hccp.org/test/cookieTest.jsp");
 * <p/>
 * . . .
 * <p/>
 * // getting cookies:
 * URLConnection conn = url.openConnection();
 * conn.connect();
 * <p/>
 * // setting cookies
 * cm.readCookies(conn);
 * cm.writeCookies(url.openConnection());
 * </code>
 *
 * @author <a href="mailto:spam@hccp.org">Ian Brown</a>
 */
public interface CookieManager
{
    //        {CookieName => {Key => Val}}
    Map<String, Map<String, String>> readCookies(URLConnection conn);

    Map<String, Map<String, String>> getCookies(URL url);

    String getCookie(URL url, String name, String key);

    void writeCookies(URLConnection conn);

    void writeCookies(URLConnection conn, Map<String, Map<String, String>> cookies);

    void setCookies(URL url, Map<String, Map<String, String>> cookies);

    void setCookie(URL url, String name, String key, String value);
}