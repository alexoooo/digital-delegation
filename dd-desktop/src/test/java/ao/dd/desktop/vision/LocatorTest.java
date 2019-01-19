package ao.dd.desktop.vision;

import ao.dd.desktop.model.image.ArrayRgbGrid;
import ao.dd.desktop.model.image.RgbGrid;
import ao.dd.desktop.util.DesktopUtils;
import ao.dd.desktop.util.Pictures;
import ao.dd.desktop.vision.feature.active_window.window_control.Xclose;
import ao.dd.desktop.vision.locate.Locator;
import ao.dd.desktop.vision.locate.SimpleLocator;
import ao.util.time.Stopwatch;

import java.awt.image.BufferedImage;


/**
 * Date: Mar 2, 2010
 * Time: 11:33:05 AM
 */
public class LocatorTest
{
    //-------------------------------------------------------------------------
//    private static final String xClose =
//                DesktopUtils.getResourceFile(
//                        "/sc1.png").toString();

    //-------------------------------------------------------------------------
//    @Test
//    public void visionTesting()
    public static void main(String[] args)
    {
//        // ScreenLayout.Screen sighting
        Locator onImage = new SimpleLocator(); // 35
////        Locator onImage = new FastLocator(); // 39
//        Locator onImage = new RgbGridLocator(); // 21, 26
//        Locator onImage = new BoyerMooreHorspoolLocator(); //

//        List<Rectangle> found, toEqual = Lists.newArrayList();

        BufferedImage screenShot = Pictures.toBufferedImage(
                DesktopUtils.getResource("/sc1.png"));
//
//        RgbGrid a = new ImageRgbGrid( screenShot );
//        int[][]    b = GridUtils.asMatrix ( screenShot );
//        RgbGrid c = new ArrayRgbGrid( screenShot );
//        BufferedImage d = c.toBufferedImage();
//
//        for (int x = 0; x < a.width(); x++)
//        {
//            for (int y = 0; y < a.height(); y++)
//            {
//                if (a.segment(x, y) != b[x][y] ||
//                        b[x][y] != c.segment(x, y))
//                {
//                    System.out.println("wtf? " + x + ", " + y);
//                }
//
//                if (d.getRGB(x, y) != c.segment(x, y))
//                {
//                    System.out.println("aha! " + x + ", " + y);
//                }
//            }
//        }
//
//
//        Scratchpad.display( screenShot );
//        Sched.sleep(1);
//        Scratchpad.display(
//                new ArrayRgbGrid(
//                        screenShot
//                ).toBufferedImage());

//        RgbGrid in     = new ImageRgbGrid(screenShot);
//        RgbGrid target =
//                new ImageRgbGrid("xClose.png", Xclose.class);
        RgbGrid in     = new ArrayRgbGrid(screenShot);
        RgbGrid target = new ArrayRgbGrid(
                "xClose.png", Xclose.class);

        Stopwatch timer = new Stopwatch();
        for (int i = 0; i < 1000; i++)
        {
            onImage.locate(in, target);
        }
        System.out.println( timer );
    }


    //-------------------------------------------------------------------------
//    @Test
//    public void testPictureDifference()
//    {
//        Locator vision = new RgbGridLocator( screen1a );
//        Area difference = vision.getDifference( screen1b,
//                new ScreenLayout( screen1b ).desktop());
//
//        assertEquals(
//                difference,
//                Areas.fromRectangles(new Rectangle(416, 104, 733, 636)),
//                "testing difference of 2 images");
//    }
}