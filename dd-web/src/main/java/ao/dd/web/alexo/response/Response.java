package ao.dd.web.alexo.response;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * A response from a UserAgent load request.
 */
public interface Response
{
    URL address();

    InputStream get();

    void setCookie(String name, String key, String value);

    String getCookie(String name, String key);

    // {CookieName => {Key => Value}}
    Map<String, Map<String, String>> getCookies();
}
