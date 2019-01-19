package ao.dd.web.apache;

import java.net.URL;

/**
 *
 */
public class ApacheTest
{
    /**
     * Main entry point to this example.
     *
     * @param args ignored
     * @throws Exception ...
     */
    public static void main(String[] args)
            throws Exception
    {
        SessionManager sessions = new ApacheSessionManager();

//        AnonProxy proxy   =
//                new AnonProxy("http://136.145.115.196:3128");
//        Session   session = proxy.open( sessions );

        Session session = sessions.open();

        String response =
                session.load(new URL("https://www.cia.gov/"));
//                session.load(new URL("http://sourceforge.net/"));
//                session.load(new URL("http://javacc.dev.java.net/"));
//                session.load(new URL("https://www.gmail.com/"));
//                session.load(new URL("http://www.google.com/"));

        System.out.println( response );
    }
}
