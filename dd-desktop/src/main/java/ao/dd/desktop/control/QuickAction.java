package ao.dd.desktop.control;

import ao.dd.desktop.control.keyboard.Keyboard;
import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.model.display.Display;
import ao.dd.desktop.model.display.Displays;
import ao.dd.desktop.model.image.ArrayRgbGrid;
import ao.dd.desktop.model.image.RgbGrid;
import ao.dd.desktop.ocr.win7resources.ForNotepad;
import ao.dd.desktop.vision.feature.active_window.window_control.Xclose;
import ao.dd.desktop.vision.locate.Locator;
import ao.dd.desktop.vision.locate.SimpleLocator;
import ao.util.time.Sched;
import com.google.common.collect.Iterables;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

import static ao.dd.desktop.util.Pictures.toBufferedImage;

/**
 * User: Eugene
 * Date: May 12, 2010
 * Time: 10:09:36 PM
 */
public class QuickAction
{
    //-------------------------------------------------------------------------
    public static void close()
    {
        find ( "xClose.png", Xclose.class, Displays.mainScreen() )
                .center().click();
    }


    //-------------------------------------------------------------------------
    public static Area find(
            String  imageFilename,
            Class   relativeTo,
            Display onDisplay)
    {
        return find(
                toBufferedImage( imageFilename, relativeTo ),
                onDisplay );
    }

    private static Area find(
            BufferedImage image,
            Display       onDisplay)
    {
        Locator locator   = new SimpleLocator();
        RgbGrid target    = new ArrayRgbGrid(image);
        Area    onScreen  = onDisplay.asArea();

        for (int i = 0; i <= 10; i++)
        {
            List<Area> found = locator.locate(onScreen, target);

            if ( ! found.equals(
                    Collections.emptyList()))
            {
                return Iterables.getOnlyElement( found );
            }
            Sched.sleep(100);
        }
        
        throw new Error();
    }


    //-------------------------------------------------------------------------
    public static void clearText()
    {
        Keyboard.type(KeyEvent.VK_CONTROL, KeyEvent.VK_A);
        Sched.sleep(10);
        Keyboard.type(KeyEvent.VK_BACK_SPACE);
    }
}
