package ao.dd.web.apache;

import ao.dd.common.WebUtils;
import ao.util.misc.Factory;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParamBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/**
 *
 */
public class ApacheSessionManager implements SessionManager
{
    //--------------------------------------------------------------------
    private static final int    CONNECT_TIMEOUT = 30 * 1000;
    private static final int    MAX_CONNECTIONS = 16;


    //--------------------------------------------------------------------
    private HttpParams              defaultParameters = null;
    private ClientConnectionManager cm;


    //--------------------------------------------------------------------
    public ApacheSessionManager()
    {
        setup();
    }


    //--------------------------------------------------------------------
    public Session open()
    {
        return new ApacheSession(new Factory<HttpClient>(){
            public HttpClient newInstance() {
                return newClient();
            }
        });
    }

    public Session open(
            String proxyProtocol,
            String proxyHost,
            int    proxyPort)
    {
        final HttpHost proxy =
                new HttpHost(proxyHost, proxyPort, proxyProtocol);
        return new ApacheSession(new Factory<HttpClient>(){
            public HttpClient newInstance() {
                HttpClient client = newClient();
                client.getParams().setParameter(
                        ConnRoutePNames.DEFAULT_PROXY, proxy);
                return client;
            }
        });
    }


    //--------------------------------------------------------------------
    private HttpClient newClient()
    {
        return new DefaultHttpClient(cm, defaultParameters);
    }


    //--------------------------------------------------------------------
    private void setup()
    {
        SchemeRegistry supportedSchemes = new SchemeRegistry();

//        org.apache.commons.h

//        Protocol.registerProtocol();

        // Register the "http" and "https" protocol schemes, they are
        // required by the default operator to look up socket factories.
        SocketFactory sf = PlainSocketFactory.getSocketFactory();
        supportedSchemes.register(new Scheme("http", sf, 80));
        sf = PlainSocketFactory.getSocketFactory();
        supportedSchemes.register(new Scheme("socks", sf, 80));
        sf = SSLSocketFactory.getSocketFactory();
        supportedSchemes.register(new Scheme("https", sf, 80));

        // prepare parameters
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(
                params, WebUtils.randomUserAgent());
        HttpProtocolParams.setUseExpectContinue(params, true);
        new ConnManagerParamBean(params)
                .setMaxTotalConnections(MAX_CONNECTIONS);
//        HttpClientParams.setConnectionManagerTimeout(
//                params, CONNECT_TIMEOUT);
        params.setLongParameter(
        		"http.connection-manager.timeout", CONNECT_TIMEOUT);
        defaultParameters = params;

        cm = new ThreadSafeClientConnManager(params, supportedSchemes);
    }


//    //--------------------------------------------------------------------
//    public static interface F

}
