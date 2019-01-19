package ao.dd.web.alexo;

import java.net.URL;
import java.util.Map;

import ao.dd.web.alexo.response.Response;

/**
 * A browser session.
 */
public interface UserAgent
{
    Response load(URL url);

    Response load(URL url, String postData);
    Response load(URL url, Map<String, String> postData);
    Response load(URL url, Map<String, String> data, boolean isPost);

    Response load(URL url, URL referer);
    Response load(URL url, URL referer, String postData);
    Response load(URL url, URL referer, Map<String, String> postData);

    Response load(URL url,
                  URL referer,
                  Map<String, String> data,
                  boolean isPost);
}
