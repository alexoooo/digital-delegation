package ao.dd.desktop.model.display;

import ao.dd.desktop.control.keyboard.Keyboard;
import ao.dd.desktop.control.mouse.Mouse;
import ao.util.time.Sched;

import java.awt.*;

/**
 * User: 188952
 * Date: Apr 17, 2010
 * Time: 11:40:03 PM
 */
public class Displays
{
    //-------------------------------------------------------------------------
    public static void main(String[] args)
    {
        for (int i = 0;
                 i < Displays.screenCount();
                 i++)
        {
            Displays.screen( i )
                    .point(200, 200);
            
            Sched.sleep(1000);
        }

        Mouse.click();
        Keyboard.type("hello");
    }


    //-------------------------------------------------------------------------
    private Displays() {}


    //-------------------------------------------------------------------------
    public static Display mainScreen()
    {
        return new ScreenDisplay(
                 GraphicsEnvironment
                        .getLocalGraphicsEnvironment()
                        .getDefaultScreenDevice()
               );
    }

    public static Display screen(int index)
    {
        return new ScreenDisplay(
                 GraphicsEnvironment
                        .getLocalGraphicsEnvironment()
                        .getScreenDevices()
                            [ index ]
               );
    }

    public static int screenCount()
    {
        return GraphicsEnvironment
                 .getLocalGraphicsEnvironment()
                 .getScreenDevices().length;
    }
}
