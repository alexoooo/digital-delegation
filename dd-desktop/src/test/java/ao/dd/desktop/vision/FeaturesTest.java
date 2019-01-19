package ao.dd.desktop.vision;

import org.testng.annotations.Test;


/**
 * Created by IntelliJ IDEA.
 * User: 188952
 * Date: Mar 2, 2010
 * Time: 11:32:26 AM
 */
public class FeaturesTest
{
    //-------------------------------------------------------------------------

//    This set of tests has to be adjusted for each system
//    because features is dependent of screen size and screen layout.

    //-------------------------------------------------------------------------
    @Test
    public void screenTest()
    {
//        assertEquals(
//                new ScreenLayout(
//                        "screenshots/samples/screenshot2.png"
//                ).screen().toRectangle(),
//                new Rectangle(0, 0, 1280, 800),
//                "screen size is incorrect");
    }

    @Test
    public void desktopTest()
    {
//        Area desktop = new ScreenLayout(
//                DesktopUtils.getResourceFile(
//                        "/img/screen/screenshot2.png")
//        ).desktop();
//
//        assertEquals(new Rectangle(106, 0, 1174, 800),
//                     desktop.toRectangle(),
//                     "desktopSize is incorrect");

////    locate on desktop
//        Assert.assertEquals("locating center of xClose button on desktop (window needs to be maximized)",
//            Pixels.surroundingArea(1256, 22),
//            desktop.locate(
//                    "screenshots/current_window_XcloseButton.png"
//            ).center());
    }
                                          
//    @Test
//    public void taskBarTest()
//    {
//        TaskBar taskBar = new TaskBar();
//        assertEquals("taskBarSize is incorrect",
//                new Rectangle(0, 0, 105, 800),
//                taskBar.toRectangle());
//    }
}
