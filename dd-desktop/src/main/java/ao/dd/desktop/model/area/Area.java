package ao.dd.desktop.model.area;

import ao.dd.desktop.control.mouse.target.MouseTarget;
import ao.dd.desktop.model.display.Display;
import ao.dd.desktop.model.image.RgbGrid;
import ao.dd.desktop.model.pixel.Pixel;
import ao.dd.desktop.vision.feature.taskbar.border.Orientation;
import java.awt.Rectangle;


/**
 * Created by IntelliJ IDEA.
 * User: 188952
 * Date: Feb 26, 2010
 * Time: 10:41:09 PM
 */

public interface Area
        extends MouseTarget
{
    //-------------------------------------------------------------------------
    public Display display();


    //-------------------------------------------------------------------------
    public int x();

    public int y();

    public Pixel center();

    public int width();

    public int height();


    //-------------------------------------------------------------------------
    public Area left();

    public Area right();

    public Area above();

    public Area below();


    //-------------------------------------------------------------------------
    public Pixel topRight();

    public Pixel bottomLeft();

    public Pixel topLeft();

    public Pixel bottomRight();


    //-------------------------------------------------------------------------
//    public Area offset(Pixel delta);
    public Area offset(int deltaX, int deltaY);

    public Area intersect(Area withArea);

    public Area getLine( Orientation direOrientation );

    //-------------------------------------------------------------------------
    public RgbGrid capture();


    //-------------------------------------------------------------------------
    public Rectangle toRectangle();


    //-------------------------------------------------------------------------
    public boolean isEmpty();


    //-------------------------------------------------------------------------
//    public Area subtract(Area minus);
}