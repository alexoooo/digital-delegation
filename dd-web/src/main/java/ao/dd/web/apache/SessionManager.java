package ao.dd.web.apache;

/**
 *
 */
public interface SessionManager
{
    public Session open();

    public Session open(String proxyProtocol,
                        String proxyHost,
                        int    proxyPort);
}
