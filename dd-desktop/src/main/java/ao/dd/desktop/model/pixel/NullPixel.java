package ao.dd.desktop.model.pixel;

import ao.dd.desktop.control.mouse.MouseButton;
import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.model.display.Display;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: 188952
 * Date: Feb 27, 2010
 * Time: 12:10:52 AM
 */
class NullPixel
        implements Pixel
{
    //-------------------------------------------------------------------------
    private NullPixel() {}


    //-------------------------------------------------------------------------
    private static final NullPixel
            INSTANCE = new NullPixel();


    //-------------------------------------------------------------------------
    public static NullPixel get()
    {
        return INSTANCE;
    }


    //-------------------------------------------------------------------------
    @Override
    public Display display() {
        return null;
    }


    //-------------------------------------------------------------------------
    @Override
    public int x() { return -1; }

    @Override
    public int y() { return -1; }


    //-------------------------------------------------------------------------
//    @Override
//    public Pixel offset(Pixel that)
//    {
////        return Pixels.nullPixel();
//        return this;
//    }

    @Override
    public Pixel offset(int deltaX, int deltaY)
    {
        return this;
    }



    //-------------------------------------------------------------------------
    @Override
    public Color colour() {
        return null;
    }


    //-------------------------------------------------------------------------
    @Override
    public int quadrant()
    {
        return 0;
    }


    //-------------------------------------------------------------------------
    @Override
    public Point toPoint()
    {
        return null;
    }

    @Override
    public Area toArea()
    {
        return null;
    }
    
    @Override
    public Area toArea(int width, int height)
    {
        return null;
    }


    //-------------------------------------------------------------------------
    @Override public void click  (                     ) {}
    @Override public void click  (int         nTimes   ) {}
    @Override public void click  (MouseButton button   ) {}
    @Override public void press  (MouseButton button   ) {}
    @Override public void release(MouseButton button   ) {}
    @Override public void scroll (int         direction) {}
    @Override public void move   (                     ) {}


    //-------------------------------------------------------------------------
    @Override
    public Rectangle toRectangle(int width, int height)
    {
        return null;
    }


    //-------------------------------------------------------------------------
    @Override
    public boolean isNull() {
        return true;
    }


    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "[null]";
    }
}
