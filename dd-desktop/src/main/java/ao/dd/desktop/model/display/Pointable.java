package ao.dd.desktop.model.display;

import com.google.common.collect.Lists;
import java.awt.Point;
import java.util.List;

/**
 * User: alex
 * Date: 19-May-2010
 * Time: 7:34:21 PM
 */
public interface Pointable
{
    //-------------------------------------------------------------------------
    public boolean point(int x, int y);


    //-------------------------------------------------------------------------
    public static class Recorder
            implements Pointable
    {
        public Recorder() {}

        public final List<Point> pointed = Lists.newArrayList();

        @Override
        public synchronized boolean point(int x, int y)
        {
            pointed.add(new Point(x, y));
            return true;
        }

        public synchronized List<Point> pointed()
        {
            return Lists.newArrayList( pointed );
        }

        @Override
        public synchronized String toString()
        {
            return pointed.toString();
        }
    }


    //-------------------------------------------------------------------------
    public static class Dummy
            implements Pointable
    {
        public Dummy() {}

        @Override
        public boolean point(int x, int y)
        {
            return true;
        }

        @Override
        public String toString()
        {
            return "Dummy";
        }
    }
}
