package ao.dd.desktop.model.area;

import ao.dd.desktop.control.mouse.target.AbstractMouseTarget;
import ao.dd.desktop.model.display.Display;
import ao.dd.desktop.model.image.RgbGrid;
import ao.dd.desktop.model.pixel.Pixel;
import ao.dd.desktop.model.pixel.Pixels;
import ao.dd.desktop.vision.feature.taskbar.border.Orientation;
import java.awt.Rectangle;


import static java.lang.Integer.MAX_VALUE;

/**
 * Created by IntelliJ IDEA.
 * User: 188952
 * Date: Feb 26, 2010
 * Time: 11:04:06 PM
 */
class SingleArea
        extends    AbstractMouseTarget
        implements Area
{
    //-------------------------------------------------------------------------
    private final Display   display;
    private final Rectangle area;


    //-------------------------------------------------------------------------
    public SingleArea(
            Display   display,
            Rectangle area)
    {
        this.display = display;
        this.area    = area;
    }


    //-------------------------------------------------------------------------
    @Override
    public Display display() {
        return display;
    }


    //-------------------------------------------------------------------------
    @Override
    public int x()
    {
        return area.x;
    }

    @Override
    public int y()
    {
        return area.y;
    }

    @Override
    public int width()
    {
        return area.width;
    }

    @Override
    public int height()
    {
        return area.height;
    }


    //-------------------------------------------------------------------------
    @Override
    public Area intersect(Area withArea)
    {
        return (withArea.isEmpty())
                ?Areas.empty()
                : Areas.newInstance(
                     display,
                     area.intersection(
                             withArea.toRectangle()));
    }


    //-------------------------------------------------------------------------
    @Override
    public Area getLine ( Orientation direction )
    {
        switch ( direction )
        {
            case LEFT:   return below();
            case RIGHT:  return below();
            case TOP:    return right();
            case BOTTOM: return right();
            default:     return Areas.empty();
        }
    }


    //-------------------------------------------------------------------------
    @Override
    public RgbGrid capture() {
        return display.view( area );
    }


    //-------------------------------------------------------------------------
    @Override
    public Pixel topLeft() {
        return Pixels.newInstance(
                 display(), x(), y());
    }

    @Override
    public Pixel topRight() {
        return Pixels.newInstance(
                 display(),
                 x() + width() - 1,
                 y());
    }

    @Override
    public Pixel bottomLeft() {
        return Pixels.newInstance(
                 display(),
                 x(),
                 y() + height() - 1);
    }

    @Override
    public Pixel bottomRight() {
        return Pixels.newInstance(
                 display(),
                 x() + width () - 1,
                 y() + height() - 1);
    }

    public Pixel center() {
        return Pixels.newInstance(
                 display(),
                 x() + (width () - 1) / 2,
                 y() + (height() - 1) / 2);
    }


    //-------------------------------------------------------------------------
    @Override
    public Area offset(int deltaX, int deltaY) {
        Rectangle offsetArea = (Rectangle) area.clone();

        offsetArea.translate(
                deltaX, deltaY);

        return Areas.newInstance(
                display,
                offsetArea);
    }


    //-------------------------------------------------------------------------
    @Override
    public Area left()
    {
        return Areas.newInstance(
                 display,
                 0, area.y, area.x, area.height);
    }

    @Override
    public Area right()
    {
        return Areas.newInstance(
                 display(),
                 new Rectangle(
                         x() + width(),
                         y(),
                         MAX_VALUE,
                         height()
                 ).intersection(
                         display().asRectangle()));
    }

    @Override
    public Area above()
    {
        return Areas.newInstance(
                 display(), x(), 0, width(), y());
    }

    @Override
    public Area below()
    {
        return Areas.newInstance(
                 display(),
                 new Rectangle(
                         x(),
                         y() + height(),
                         width(),
                         MAX_VALUE
                 ).intersection(
                         display().asRectangle()));
    }


    //-------------------------------------------------------------------------
    @Override
    public void move()
    {
        center().move();
    }


    //-------------------------------------------------------------------------
    @Override
    public Rectangle toRectangle()
    {
        return (Rectangle) area.clone(); 
    }


    //-------------------------------------------------------------------------
    @Override
    public boolean isEmpty() {
        return false;
    }


    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "("   + area.x     + ", "   + area.y      + ") " +
               "[w " + area.width + ", h " + area.height + "]";
    }


    //-------------------------------------------------------------------------
    @Override
    public boolean equals(Object o)
    {
        if (o == null ||
                !(o instanceof Area))
        {
            return false;
        }

        Area that = (Area) o;

        return display().equals( that.display() ) &&
               x     () == that.x     () &&
               y     () == that.y     () &&
               width () == that.width () &&
               height() == that.height();
    }
}
