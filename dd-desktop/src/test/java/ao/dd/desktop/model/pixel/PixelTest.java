package ao.dd.desktop.model.pixel;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.model.area.Areas;
import ao.dd.desktop.model.display.Displays;
import org.testng.annotations.Test;

import java.awt.*;

import static ao.dd.desktop.model.pixel.Pixels.newInstance;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;


/**
 * Created by IntelliJ IDEA.
 * User: 188952
 * Date: Feb 27, 2010
 * Time: 4:09:13 AM
 */
public class PixelTest
{
    //-------------------------------------------------------------------------
//    @Test
//    public void compareToYourself()
//    {
//        Pixel original = Pixels.surroundingArea(0, 0);
//        Pixel prototype = original.prototype();
//
//        assertEquals(
//                prototype,
//                original,
//                "Comparing a Pixel to it's clone.");
//
//        assertEquals(
//                prototype.hashCode(),
//                original.hashCode(),
//                "Comparing hash codes clone.");
//    }

    //-------------------------------------------------------------------------
//    @Test
//    public void pixelOffsetTest()
//    {
//        Pixel original = Pixels.surroundingArea(8, 1);
//
//        assertEquals(
//                Pixels.nullPixel().prototype(),
//                Pixels.nullPixel(),
//                "prototype of nullPixel.");
//    }

    //-------------------------------------------------------------------------
    @Test
    public void nullPixelTesting()
    {
//        assertFalse(Pixels.nullPixel().move(),
//                    "moving to nullPixel");

        assertNull(Pixels.nullPixel().toPoint(),
                   "converting nullPixel to Point");

        assertNull(Pixels.nullPixel().toRectangle(10, 10),
                   "Rectangle made from nullPixel");

        assertEquals(
                Pixels.nullPixel().offset(10, 10),
                Pixels.nullPixel(),
                "offset nullPixel");

        assertEquals(
                Pixels.nullPixel().toString(),
                "[null]",
                "nullPixel to String");
    }


    //-------------------------------------------------------------------------
    @Test
    public void boundedPixelTest()
    {
        Pixel defaultBounded = Pixels.newInstance();

        assertEquals(
                defaultBounded,
                Pixels.nullPixel(),
                "create default BoundedPixel");

        assertEquals(Pixels.newInstance(
                Displays.mainScreen(),
                5, 5)
                .offset(11, 11),
                Pixels.newInstance(
                        Displays.mainScreen(),
                        16, 16),
                "offset a BoundedPixel");
    }


    //-------------------------------------------------------------------------
    @Test
    public void pixelsToArea()
    {
        Pixel topLeft     = newInstance ( Displays.mainScreen(),   1,    1 );
        Pixel bottomRight = newInstance ( Displays.mainScreen(), 901, 1001 );
        Pixel leftMost    = newInstance ( Displays.mainScreen(), 500,    1 );
        Pixel rightMost   = newInstance ( Displays.mainScreen(), 500, 1001 );
        Pixel topMost     = newInstance ( Displays.mainScreen(),   4,  500 );
        Pixel bottomMost  = newInstance ( Displays.mainScreen(), 904,  500 );
        Pixel middle1     = newInstance ( Displays.mainScreen(), 500,  500 );
        Pixel middle2     = newInstance ( Displays.mainScreen(), 400,  400 );
        Pixel nullPixel   = Pixels.nullPixel();

        // from null pixels
        Area aroundPixels = Areas.surroundingArea(( Pixel[] ) null );

        assertEquals(
                aroundPixels,
                Areas.empty(),
                "zero (null) pixels should make an EmptyArea.");

        // from 1 nullPixel
        aroundPixels = Areas.surroundingArea(nullPixel);

        assertEquals(
                aroundPixels,
                Areas.empty(),
                "1 nullPixel should make an EmptyArea.");

        // from 1 valid pixel
        aroundPixels = Areas.surroundingArea(topMost);

        assertEquals(
                aroundPixels,
                Areas.newInstance(
                        Displays.mainScreen(),
                        new Rectangle(
                                topMost.toPoint(),
                                new Dimension(1, 1))),
                "1 pixel should make a 1 pixel-singleArea.");

        // from 2 valid pixels
        aroundPixels = Areas.surroundingArea(
                         topLeft, bottomRight);

        assertEquals(
                aroundPixels,
                Areas.newInstance(
                        Displays.mainScreen(),
                        new Rectangle(
                                1, 1, 901, 1001)),
                "2 pixels should make 1 singleArea.");

        // from 3 valid pixels
        aroundPixels = Areas.surroundingArea(
                topLeft,
                bottomRight,
                newInstance(
                        Displays.mainScreen(),
                        1301, 501));

        assertEquals(
                aroundPixels,
                Areas.newInstance(
                        Displays.mainScreen(),
                        new Rectangle(
                                1, 1, 1301, 1001)),
                "3 pixels should make 1 singleArea");

        // from ALL Possible pixels at once
        aroundPixels = Areas.surroundingArea(
                leftMost, rightMost,
                topMost, bottomMost,
                middle1, middle2,
                nullPixel, null);

        assertEquals(
                aroundPixels,
                Areas.newInstance(
                        Displays.mainScreen(),
                        new Rectangle(4, 1, 901, 1001)),
                "Checking if surroundingArea of pixels is correct.");
    }

    //-------------------------------------------------------------------------
//    public static Pixel surroundingArea(
//            int x, int y)
//    {
//        return Pixels.surroundingArea(
//                 Displays.mainScreen(),
//                 x, y);
//    }
}
