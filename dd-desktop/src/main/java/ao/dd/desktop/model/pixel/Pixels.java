package ao.dd.desktop.model.pixel;

import ao.dd.desktop.model.display.Display;

/**
 * Created by IntelliJ IDEA.
 * User: 188952
 * Date: Feb 27, 2010
 * Time: 1:12:01 AM
 */
public class Pixels
{
    //-------------------------------------------------------------------------


    //-------------------------------------------------------------------------
    private Pixels() {}


    //-------------------------------------------------------------------------
    public static Pixel nullPixel()
    {
        return NullPixel.get();
    }

    public static Pixel newInstance()
    {
        return NullPixel.get();
    }

    public static Pixel newInstance(
            Display display, int x, int y)
    {
        return new BoundedPixel(
                display, x, y);
    }
}
