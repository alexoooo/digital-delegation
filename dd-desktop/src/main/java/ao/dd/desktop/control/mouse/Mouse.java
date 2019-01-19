package ao.dd.desktop.control.mouse;

import ao.dd.desktop.util.DesktopUtils;
import ao.util.time.Sched;
import java.awt.Robot;

/**
 * User: 188952
 * Date: Apr 18, 2010
 * Time: 12:41:02 AM
 */
public class Mouse
{
    //-------------------------------------------------------------------------
    private Mouse() {}


    //-------------------------------------------------------------------------
    private static final Robot BOT = DesktopUtils.newRobot();

    
    //-------------------------------------------------------------------------
    private static long actionAutoDelay = 250;
    
    
    //-------------------------------------------------------------------------
    public static synchronized long setActionAutoDelay(long delay)
    {
        long prev = actionAutoDelay;
        actionAutoDelay = delay;
        return prev;
    }
    
    private static void sleepAction()
    {
        Sched.sleep( actionAutoDelay );
    }
    

    //-------------------------------------------------------------------------
    public static synchronized void click(int nTimes)
    {
        for (int i = 0; i < nTimes; i++)
        {
            click();
        }
    }

    public static synchronized void click()
    {
        click( MouseButton.BUTTON_1 );
    }

    public static synchronized void click(MouseButton button)
    {
        sleepAction();
        press  ( button );
        release( button );
    }


    //-------------------------------------------------------------------------
    public static synchronized void press(MouseButton button)
    {
        BOT.mousePress  ( button.inputEvent() );
    }

    public static synchronized void release(MouseButton button)
    {
        BOT.mouseRelease( button.inputEvent() );
    }


    //-------------------------------------------------------------------------
    public static synchronized void scroll(int direction)
    {
        BOT.mouseWheel( direction );
    }
}
