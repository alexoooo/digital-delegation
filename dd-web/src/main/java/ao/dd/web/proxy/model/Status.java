package ao.dd.web.proxy.model;

/**
 * Proxy server statistics
 */
public class Status
{
    //--------------------------------------------------------------------
    private static final long ELIGIBLE_AFTER = 3 * 60 * 1000L;


    //--------------------------------------------------------------------
    private final int  FAIL_COUNT;
    private final int  PASS_COUNT;
    private final long RUNTIME;
    private final long LAST_RUN;


    //--------------------------------------------------------------------
    public Status(String csv)
    {
        int  firstComma = csv.indexOf(','                 );
        int secondComma = csv.indexOf(',',  firstComma + 1);
        int   lastComma = csv.indexOf(',', secondComma + 1);

        FAIL_COUNT = Integer.parseInt(csv.substring(0, firstComma));
        PASS_COUNT = Integer.parseInt(csv.substring(
                            firstComma + 1, secondComma));
        RUNTIME = Long.parseLong(csv.substring(
                            secondComma + 1, lastComma));
        LAST_RUN = Long.parseLong(csv.substring(lastComma + 1));
    }

    public Status()
    {
        this(0, 0, 0, 0);
    }

    private Status(int  successes,
                   int  fails,
                   long totalRuntime,
                   long lastRunAt)
    {
        PASS_COUNT = successes;
        FAIL_COUNT = fails;
        RUNTIME = totalRuntime;
        LAST_RUN = lastRunAt;
    }


    //--------------------------------------------------------------------
    public Status update(boolean success, long runtime)
    {
        return new Status(
                PASS_COUNT + (success ? 1 : 0),
                FAIL_COUNT + (success ? 0 : 1),
                RUNTIME + runtime,
                System.currentTimeMillis());
    }


    //--------------------------------------------------------------------
    public synchronized String toCsv()
    {
        return FAIL_COUNT + "," +
               PASS_COUNT + "," +
               RUNTIME + "," +
               LAST_RUN;
    }


    //--------------------------------------------------------------------
    public boolean isEligible()
    {
        return (System.currentTimeMillis() - LAST_RUN) >= ELIGIBLE_AFTER;
    }

    public int trialCount()
    {
        return FAIL_COUNT + PASS_COUNT;
    }

    public boolean hasGoodRoi()
    {
        return trialCount()   <= 5 ||
               averageReward() > 0.1;
    }

    public boolean passedMore(Status than)
    {
        return PASS_COUNT > than.PASS_COUNT;
    }


    //--------------------------------------------------------------------
    public double ubc1(int totalTrials)
    {
        return trialCount() == 0
               ? Double.MAX_VALUE
               : averageReward() +
                 Math.sqrt((2 * Math.log(totalTrials))
                           / trialCount());
    }

    private double averageReward()
    {
        return ((double) PASS_COUNT) / trialCount();
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return toCsv();
    }
}
