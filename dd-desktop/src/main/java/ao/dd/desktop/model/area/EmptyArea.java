package ao.dd.desktop.model.area;

import ao.dd.desktop.control.mouse.MouseButton;
import ao.dd.desktop.model.display.Display;
import ao.dd.desktop.model.image.RgbGrid;
import ao.dd.desktop.model.pixel.Pixel;
import ao.dd.desktop.model.pixel.Pixels;
import ao.dd.desktop.vision.feature.taskbar.border.Orientation;
import java.awt.Rectangle;

/**
 * Created by IntelliJ IDEA.
 * User: 188952
 * Date: Feb 27, 2010
 * Time: 12:16:30 AM
 */
class EmptyArea
        implements Area
{
    //-------------------------------------------------------------------------
    private static Area INSTANCE = new EmptyArea();


    //-------------------------------------------------------------------------
    public static Area get()
    {
        return INSTANCE;
    }


    //-------------------------------------------------------------------------
    private EmptyArea() {}



    //-------------------------------------------------------------------------
    @Override
    public Display display() {
        return null;
    }


    //-------------------------------------------------------------------------
    @Override public int   x()      { return -1; }

    @Override public int   y()      { return -1; }

    @Override public int   width()  { return -1; }

    @Override public int   height() { return -1; }


    @Override public Pixel center() { return Pixels.nullPixel(); }


    //-------------------------------------------------------------------------
    @Override
    public Area left()
    {
        return this;
    }

    @Override
    public Area right()
    {
        return this;
    }

    @Override
    public Area above()
    {
        return this;
    }

    @Override
    public Area below()
    {
        return this;
    }


    //-------------------------------------------------------------------------
    @Override
    public Area offset(int x, int y)
    {
        return this;
    }


    //-------------------------------------------------------------------------
    @Override public Pixel topLeft()     { return Pixels.nullPixel(); }
    @Override public Pixel bottomRight() { return Pixels.nullPixel(); }
    @Override public Pixel bottomLeft()  { return Pixels.nullPixel(); }
    @Override public Pixel topRight()    { return Pixels.nullPixel(); }


    //-------------------------------------------------------------------------
    @Override public void move() {}
    @Override public void click() {}
    @Override public void click(int nTimes) {}
    @Override public void click(MouseButton button) {}
    @Override public void press(MouseButton button) {}
    @Override public void release(MouseButton button) {}
    @Override public void scroll(int direction) {}


    //-------------------------------------------------------------------------
    @Override
    public Area intersect(Area withArea)
    {
        return Areas.empty();
    }

    //-------------------------------------------------------------------------
    @Override
    public Area getLine( Orientation diOrientation )
    {
        return Areas.empty();
    }


    //-------------------------------------------------------------------------
    @Override
    public RgbGrid capture() {
        return null;
    }


    //-------------------------------------------------------------------------
    @Override
    public Rectangle toRectangle() {
        return null;
    }


    //-------------------------------------------------------------------------
    @Override
    public boolean isEmpty()
    {
        return true;
    }


    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "EmptyArea";
    }
}
