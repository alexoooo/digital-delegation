package ao.dd.shell.impl.transfer.stream;

import ao.util.async.Throttle;
import ao.util.math.Calc;

import java.io.*;

/**
 * User: aostrovsky
 * Date: 8-Jul-2009
 * Time: 12:33:32 PM
 */
public class ThrottledInputStream extends InputStream
{
    //-----------------------------------------------------------
//    private static final Logger LOG =
//            Logger.getLogger(ThrottledInputStream.class);


    //-----------------------------------------------------------
    private final InputStream deleget;
    private final Throttle    throttle;
    private final Monitor     monitor;
//    private       long        transmitted = 0;


    //-----------------------------------------------------------
    public ThrottledInputStream(
            String filename,
            long   bytesPerSecond)
            throws FileNotFoundException
    {
        this(new FileInputStream(filename),
             bytesPerSecond,
             new BlindMonitor());
    }
    public ThrottledInputStream(
            File    file,
            long    bytesPerSecond)
            throws FileNotFoundException {
        this(file, bytesPerSecond, new BlindMonitor());
    }
    public ThrottledInputStream(
            File    file,
            long    bytesPerSecond,
            Monitor progressMonitor)
            throws FileNotFoundException {
        this(new FileInputStream(file),
                bytesPerSecond, progressMonitor);
    }

    public ThrottledInputStream(
            InputStream in,
            long        bytesPerSecond)
    {
    	this(in, bytesPerSecond, new BlindMonitor());
    }
    
    public ThrottledInputStream(
            InputStream in,
            long        bytesPerSecond,
            Monitor     progressMonitor)
    {
        deleget  = (in instanceof BufferedInputStream)
                   ? in : new BufferedInputStream(in);
        throttle = new Throttle(bytesPerSecond);
        monitor  = progressMonitor;
    }


    //-----------------------------------------------------------
    public void throttle(long bytesPerSecond)
    {
        throttle.limit(bytesPerSecond);
    }


    //-----------------------------------------------------------
    public int read() throws IOException
    {
//        long remaining = Calc.unsigned(deleget.available());
//        boolean detailed = (remaining < 1000);

//        if (detailed) System.out.print(".");
//        throttle.process();
//        if (detailed) System.out.print("#");
//        int in = deleget.read();
//        if (detailed) System.out.print("!");
//        return in;

        long remaining = Calc.unsigned(deleget.available());
        if ((remaining % 1024) == 0) {
            monitor.progress(remaining);
        }

//        transmitted++;
        throttle.process();
        return deleget.read();
    }


    //-----------------------------------------------------------
    public @Override void close() throws IOException
    {
        deleget.close();
    }


    //-----------------------------------------------------------
    public static interface Monitor {
        public void progress(long remaining);
    }
    private static class BlindMonitor implements Monitor {
        public void progress(long remaining) {}
    }
}
