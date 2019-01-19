package ao.dd.shell.impl.terminal.data;

import ao.util.pass.Filter;
import ao.util.text.AoFormat;
import ao.util.time.Sched;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: aostrovsky
 * Date: 27-Jan-2010
 * Time: 11:28:09 AM
 */
public class StreamPartition
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(StreamPartition.class);

    private static final InputStream EMPTY_STREAM =
            new InputStream() {
                private static final long serialVersionUID = 420L;
                @Override public int read() throws IOException {
                    return -1;
                }
            };


    //--------------------------------------------------------------------
    private StreamPartition() {}


    //--------------------------------------------------------------------
    public static InputStream read(
            InputStream in,
            long        timeout)
    {
        return readAfter(in, (byte[]) null, timeout);
    }

    public static String toString(
            InputStream in,
            long        timeout)
    {
        try {
            return getToString(in, timeout);
        } catch (IOException e) {
            LOG.error("Unable to flatten to string", e);
            return null;
        }
    }
    private static String getToString(
            InputStream in,
            long        timeout) throws IOException
    {
        StringBuilder str   = new StringBuilder();
        long          start = System.currentTimeMillis();
        while (true) {
            int val = readIfAvailable(in, start, timeout);
            if (val == -1) break;
            str.append((char) val);
        }
        return str.toString();
    }


    //--------------------------------------------------------------------
    public static InputStream readAfter(
            InputStream in,
            String      sentinelStart,
            long        timeout)
    {
        return readAfter(in, stringBytes(sentinelStart), timeout);
    }

    public static InputStream readAfter(
            InputStream in,
            byte[]      sentinelStart,
            long        timeout)
    {
        try {
            return doReadAfter(in, sentinelStart, timeout);
        } catch (IOException e) {
            LOG.error("Unable to readAfter " + in, e);
            return EMPTY_STREAM;
        }
    }

    private static InputStream doReadAfter(
            final InputStream in,
            final byte[]      sentinelStart,
            final long        timeout)
                    throws IOException
    {
        final long startTime = System.currentTimeMillis();
        if (sentinelStart == null || sentinelStart.length == 0) {
            return in;
        }

        FiniteQueue backBuffer =
                new FiniteQueue( sentinelStart.length );

        long skipped = 0;
        while (true) {
            if (timedOut(startTime, timeout)) {
                return EMPTY_STREAM;
            }

            int val = readIfAvailable(in, startTime, timeout);
            if (val == -1) {
                return EMPTY_STREAM;
            }

            backBuffer.addDestructive((byte) val);
            if (backBuffer.equals( sentinelStart )) {
                break;
            }

            skipped++;
        }

        LOG.trace("reading after " + skipped);
        return in;
    }


    //--------------------------------------------------------------------
    public static InputStream readBefore(
            InputStream in,
            String      sentinelEnd,
            long        timeout)
    {
        return readBefore(in, stringBytes(sentinelEnd), timeout);
    }

    public static InputStream readBefore(
            InputStream in,
            byte[]      sentinelEnd,
            long        timeout)
    {
        try {
            return doReadBefore(in, sentinelEnd, timeout);
        } catch (IOException e) {
            LOG.error("Unable to readAfter " + in, e);
            return EMPTY_STREAM;
        }
    }

    private static InputStream doReadBefore(
            final InputStream in,
            final byte[]      sentinelEnd,
            final long        timeout)
                    throws IOException
    {
        final long startTime = System.currentTimeMillis();
        if (sentinelEnd == null || sentinelEnd.length == 0) {
            return in;
        }

        final FiniteQueue forwardBuffer =
                new FiniteQueue( sentinelEnd.length );

        while (forwardBuffer.size() < sentinelEnd.length) {
            if (timedOut(startTime, timeout)) {
                return EMPTY_STREAM;
            }

            int val = readIfAvailable(in, startTime, timeout);
            if (val == -1) {
                return EMPTY_STREAM;
            }

            forwardBuffer.add((byte) val);
        }

        return new BufferedInputStream(new InputStream() {
            private long read = 0;
            @Override public int read() throws IOException {
                if (timedOut(startTime, timeout) ||
                        forwardBuffer.equals(sentinelEnd)) {
                    return -1;
                }

                int val = readIfAvailable(in, startTime, timeout);
                if (val == -1) {
                    return val;
                }

                if ((++read % (128 * 1024)) == 0) {
                    LOG.trace("read " + AoFormat.decimal(read)
                                      + "\t" + new DateTime());
                }

                return forwardBuffer.addDestructive(
                         (byte) val);
            }
            
            @Override public int available() throws IOException {
            	return Math.max(1, in.available());
            }
        });
    }


    //--------------------------------------------------------------------
    public static InputStream readBetween(
            InputStream in,
            String      sentinelStart,
            String      sentinelEnd,
            long        timeout)
    {
        return readBetween(
                 in,
                 stringBytes(sentinelStart),
                 stringBytes(sentinelEnd),
                 timeout);
    }

    public static InputStream readBetween(
            InputStream in,
            byte[]      sentinelStart,
            byte[]      sentinelEnd,
            long        timeout)
    {
        return readBefore(
                readAfter(in, sentinelStart, timeout),
                sentinelEnd,
                timeout);
    }


    //-------------------------------------------------------------------------
    public static void scan(
            final InputStream    in,
            final Filter<String> filter,
            final String...      targets)
    {
        byte[][] byteTargets = new byte[ targets.length ][];
        for (int i = 0; i < byteTargets.length; i++) {
            byteTargets[ i ] = targets[ i ].getBytes();
        }

        scan(in, new Filter<byte[]>() {
            @Override public boolean accept(byte[] target) {
                return filter.accept(new String(target));
            }
        }, byteTargets, Integer.MAX_VALUE);
    }

    /**
     * Looks at a given InputStream, one byte at a time, and alerts the
     *  given filter each time any of the given targets are seen.
     * Keeps running while:
     *   - the stream has data
     *   - a timeout has not been reached
     *   - the filter did not return true to any of the matched targets.
     *
     * @param in input stream to scan one character at a time.
     * @param filter will be notified of matched targets,
     *                 returns true to stop scan.
     * @param targets will be matched against the stream, and
     *                  the filter will be notified.
     * @param timeout the maximum amount of time that the scan will take.
     */
    public static void scan(
            InputStream    in,
            Filter<byte[]> filter,
            byte[][]       targets,
            long           timeout)
    {
        try {
            doScan(in, filter, targets, timeout);
        } catch (IOException e) {
            LOG.error("Unable to scan", e);
        }
    }

    private static void doScan(
            InputStream    in,
            Filter<byte[]> listener,
            byte[][]       targets,
            long           timeout) throws IOException
    {
        assert targets.length != 0;

        int maxTargetLength = -1;
        for (byte[] target : targets)
        {
            assert target.length != 0;
            maxTargetLength = Math.max(
                    maxTargetLength, target.length);
        }

        FiniteQueue backBuffer = new FiniteQueue(
                maxTargetLength);

        long startTime = System.currentTimeMillis();
        while (true)
        {
            int val = readIfAvailable(in, startTime, timeout);
            if (val == -1) {
                return;
            }

            backBuffer.addDestructive((byte) val);
            for (byte[] target : targets) {
                if (backBuffer.endsWith(target)) {
                    if (listener.accept(target)) {
                        return;
                    }
                }
            }
        }
    }


    //--------------------------------------------------------------------
    private static byte[] stringBytes(String value)
    {
        return (value == null)
               ? null : value.getBytes();
    }

    private static boolean timedOut(
            long startingFrom, long timeout)
    {
        long delta = (System.currentTimeMillis() - startingFrom);
        return delta > timeout;
    }

    public static int readIfAvailable(
            InputStream in,
            long        startedAt,
            long        timeout) throws IOException
    {
//        int tries = 0;
        while (! timedOut(startedAt, timeout)) {
            if (in.available() > 0) {
                return in.read();
            }
            Sched.sleep(10);
        }
        return -1;
    }

    private static char asChar(byte val)
    {
        return (char) (val & 0xFF);
    }
}
