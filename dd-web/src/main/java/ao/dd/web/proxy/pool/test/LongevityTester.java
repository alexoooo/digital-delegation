package ao.dd.web.proxy.pool.test;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import ao.dd.web.apache.Session;
import ao.dd.web.proxy.pool.ProxyPool;
import ao.dd.web.proxy.pool.ProxyPoolImpl;
import ao.util.math.crypt.MD5;
import ao.util.math.crypt.SecureHash;

/**
 * Date: Dec 21, 2008
 * Time: 6:14:10 PM
 */
public class LongevityTester
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(LongevityTester.class);

    private static final Set<String> seenQueries =
            new ConcurrentSkipListSet<String>();


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        new LongevityTester().stressTest(
                60, new Duration(8 * 60 * 60 * 1000));

        System.exit(0);
    }
    public void stressTest(
            int      chunksize,
            Duration timespan)
    {
        LOG.info("starting test for " + timespan +
                 " millis, " + chunksize + " requests at a time");

        ProxyPool proxyPool = ProxyPoolImpl.newInstance();

        int     count = 0;
        Instant start = new Instant();
        while (start.plus( timespan ).isAfterNow())
        {
            Instant roundStart = new Instant();
            proxyPool.submitAndWait(randomWorkers(chunksize));

            LOG.info("finished round " + (count++) +
                     " in " + new Duration(roundStart,   new Instant()) +
                     " totalling " + new Duration(start, new Instant()));
        }
        LOG.info("test completed successfully in " +
                 new Duration(start, new Instant()));
    }

    private static List<ProxyPool.Worker> randomWorkers(int howMany)
    {
        List<ProxyPool.Worker> workers =
                new ArrayList<ProxyPool.Worker>();
        for (int i = 0; i < howMany; i++)
        {
            workers.add(randomWorker());
        }
        return workers;
    }

    private static ProxyPool.Worker randomWorker()
    {
        return new ProxyPool.Worker() {
                @SuppressWarnings("serial")
				public void work(Session with) throws Throwable {
                    long before = System.currentTimeMillis();

                    final String query = randomQuery();
                    with.load(
                            new URL("http://www.google.com"),
                            new HashMap<String, String>(){{
                                put("q", query);
                            }},
                            false);
                    if (! seenQueries.add( query ))
                    {
                        LOG.error("already worked on" + query);
                    }

                    long after = System.currentTimeMillis();
                    LOG.info("loading '" + query + "' took " +
                             (after - before));
                }
            };
    }

    private static String randomQuery()
    {
        SecureHash hash = new MD5();

        hash.feed( System.nanoTime() );
//        hash.feed( System.currentTimeMillis() );
//        hash.feed( Rand.nextLong() );

        return hash.hexDigest();

        //return Word.random(10 + Rand.nextInt(5));
    }
}
