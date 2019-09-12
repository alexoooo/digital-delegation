package ao.dd.desktop.control.mouse.destination;

import ao.dd.desktop.control.mouse.MouseButton;

/**
 * User: 188952
 * Date: Apr 18, 2010
 * Time: 1:18:15 AM
 *
 * First moves, then performs the given action.
 */
public interface MouseTarget
{
    //-------------------------------------------------------------------------
    public void move();


    //-------------------------------------------------------------------------
    public void click();
    public void click(int nTimes);
    public void click(MouseButton button);


    //-------------------------------------------------------------------------
    public void press  (MouseButton button);
    public void release(MouseButton button);


    //-------------------------------------------------------------------------
    public void scroll(int direction);
}
