package ao.dd.web.alexo;

import ao.dd.common.WebUtils;

import java.io.IOException;

/**
 * See
 *  http://tools.rosinstrument.com/cgi-bin/fp.pl/
 *      showlines?lines=20&sortor=3&filter=HTTP
 */
public class ProxyTest
{
    public static void main(String[] args) throws IOException
    {
//        List<Proxy> proxies = Arrays.asList(
//                new Proxy(Type.HTTP, new InetSocketAddress(
//                        "righthand.eecs.harvard.edu", 3128)),
//                new Proxy(Type.HTTP, new InetSocketAddress(
//                        "204.13.160.129", 80)),
//                new Proxy(Type.HTTP, new InetSocketAddress(
//                        "136.145.115.196", 3128)),
//                new Proxy(Type.HTTP, new InetSocketAddress(
//                        "pl1.unm.edu", 3128)),
//                new Proxy(Type.HTTP, new InetSocketAddress(
//                        "205.189.33.178", 3128)));


        UserAgent ua = new UserAgentImpl();

        System.out.println(
                ua.load(WebUtils.asUrl(
                        "http://www.google.com")));

//        System.out.println(
//                ua.load(WebUtils.asUrl(
//                        "https://www.cia.gov/")));
//        System.out.println(
//                ua.load(WebUtils.asUrl(
//                        "https://www.gmail.com/")));

        //http://righthand.eecs.harvard.edu:3128
    }
}
