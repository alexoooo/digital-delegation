package ao.dd.desktop.control.keyboard.dynamics;

/**
 * User: 188952
 * Date: Apr 18, 2010
 * Time: 1:03:51 AM
 */
public interface KeyboardDynamics
{
    //-------------------------------------------------------------------------
    public long pressPause  (int fromKeyEvent, int toKeyEvent);
    public long releasePause(int keyEvent);
}
