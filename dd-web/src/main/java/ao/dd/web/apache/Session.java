package ao.dd.web.apache;

import java.net.URL;
import java.util.Map;

/**
 * HTTP session for loading text data.
 */
public interface Session
{
    public String load(URL address);

    public String load(URL                 address,
                       Map<String, String> data,
                       boolean             isPost);

    public String load(URL                 address,
                       URL                 referer,
                       Map<String, String> data,
                       boolean             isPost);
}
