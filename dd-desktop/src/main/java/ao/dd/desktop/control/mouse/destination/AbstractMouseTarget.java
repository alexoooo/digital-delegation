package ao.dd.desktop.control.mouse.destination;

import ao.dd.desktop.control.mouse.Mouse;
import ao.dd.desktop.control.mouse.MouseButton;

/**
 * User: 188952
 * Date: Apr 18, 2010
 * Time: 1:23:30 AM
 */
public abstract class AbstractMouseTarget
        implements MouseTarget
{
    //-------------------------------------------------------------------------
    @Override
    public abstract void move();


    //-------------------------------------------------------------------------
    @Override
    public void click() {
        move();
        Mouse.click();
    }

    @Override
    public void click(int nTimes) {
        move();
        Mouse.click(nTimes);
    }

    @Override
    public void click(MouseButton button) {
        move();
        Mouse.click(button);
    }

    @Override
    public void press(MouseButton button) {
        move();
        Mouse.press(button);
    }

    @Override
    public void release(MouseButton button) {
        move();
        Mouse.release(button);
    }

    @Override
    public void scroll(int direction) {
        move();
        Mouse.scroll(direction);
    }
}
