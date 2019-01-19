package ao.dd.desktop.model.area;

import ao.dd.desktop.model.display.Display;
import ao.dd.desktop.model.pixel.Pixel;
import ao.dd.desktop.model.pixel.Pixels;

import java.awt.*;
import java.util.Arrays;

import static java.lang.Integer.MAX_VALUE;

/**
 * Created by IntelliJ IDEA.
 * User: 188952
 * Date: Feb 27, 2010
 * Time: 12:15:57 AM
 */
public class Areas
{
    //-------------------------------------------------------------------------


    //-------------------------------------------------------------------------
    private Areas() {}


    //-------------------------------------------------------------------------
    public static Area empty()
    {
        return EmptyArea.get();
    }


    //-------------------------------------------------------------------------
//    public static Area surroundingArea(
//            Rectangle rectangle)
//    {
//        return surroundingArea(
//                Displays.mainScreen(),
//                rectangle);
//    }

    public static Area newInstance(
            Display display,
            int     x,
            int     y,
            int     width,
            int     height)
    {
        return newInstance(
                display,
                new Rectangle(
                        x, y, width, height
                ));
    }

    public static Area newInstance(
            Display   display,
            Rectangle rectangle)
    {
        return rectangle == null ||
               rectangle.getWidth() == 0 ||
               rectangle.getHeight() == 0
               ? EmptyArea.get()
               : new SingleArea(
                   display, rectangle);
    }


    //-------------------------------------------------------------------------
//    public static Area fromAreas(Area... components)
//    {
//        return fromAreas(Arrays.asList( components ));
//    }

//    public static Area fromAreas(Iterable<Area> components)
//    {
//        return fromAreas( components.iterator() );
//    }
//
//    public static Area fromAreas(Iterator<Area> components)
//    {
//        if (components == null) return EmptyArea.get();
//
//        if (! components.hasNext()) return EmptyArea.get();
//
//        Area first = components.next();
//        if (components.hasNext())
//        {
//            throw new UnsupportedOperationException();
//        }
//        else
//        {
//            return first;
//        }
//    }

//    public static Area from(BufferedImage image)
//    {
//        return Areas.fromRectangles(new Rectangle(
//                0,0, image.getWidth(), image.getHeight()));
//    }

//    public static Area screen()
//    {
//        return Areas.from(GlobalRobot.takeScreenShot());
//    }


    //-------------------------------------------------------------------------
    public static Area surroundingArea(Pixel... fromPixels)
    {
        if (fromPixels == null ||
            fromPixels.length == 0)
        {
            return Areas.empty();
        }

        //fromPixels[.equals(Pixels.nullPixel())

        return surroundingArea(
                Arrays.asList( fromPixels ));
    }

    public static Area surroundingArea(Iterable<Pixel> fromPixels)
    {
        int count      = 0;
        int leftMost   = MAX_VALUE;
        int topMost    = MAX_VALUE;
        int rightMost  = 0;
        int bottomMost = 0;

        Display display = null;
        for (Pixel p : fromPixels)
        {
            if (p == null || p.isNull()) continue;

            if (display == null)
            {
                display = p.display();
            }
            else
            {
                if (! display.equals( p.display() ))
                {
                    throw new RuntimeException(
                            "display mismatch: " +
                                display + " vs " + p.display());
                }
            }

            leftMost  = Math.min(p.x(), leftMost  );
            rightMost = Math.max(p.x(), rightMost );

            topMost    = Math.min(p.y(), topMost   );
            bottomMost = Math.max(p.y(), bottomMost);

            count++;
        }

        if (count == 0)
        {
            return Areas.empty();
        }

        Pixel topLeft = Pixels.newInstance(
                display, leftMost, topMost);

//        Rectangle enclosing =
//                (count[0] == 1)
//                ? topLeft.toRectangle(1, 1)
//                : topLeft.toRectangle(
//                        rightMost [0] - leftMost[0],
//                        bottomMost[0] - topMost [0]);

        Rectangle enclosing =
                topLeft.toRectangle(
                        rightMost  - leftMost + 1,
                        bottomMost - topMost  + 1
                );

//        if (enclosing.width == 0)
//        {
//            enclosing.width++;
//        }
//
//        if (enclosing.height == 0)
//        {
//            enclosing.height++;
//        }

        return Areas.newInstance(
                 display, enclosing);
    }
}
