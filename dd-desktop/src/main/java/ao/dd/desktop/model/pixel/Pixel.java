package ao.dd.desktop.model.pixel;

import ao.dd.desktop.control.mouse.destination.MouseTarget;
import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.model.display.Display;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: 188952
 * Date: Feb 27, 2010
 * Time: 12:10:43 AM
 */

public interface Pixel
        extends MouseTarget
{
    //-------------------------------------------------------------------------
    public Display display();


    //-------------------------------------------------------------------------
    public int x();

    public int y();


    //-------------------------------------------------------------------------
    public Pixel offset(int deltaX, int deltaY);


    //-------------------------------------------------------------------------
    public Color colour();


    //-------------------------------------------------------------------------
    public Point     toPoint();

    public Area      toArea     ();
    public Area      toArea     (int width, int height);
    public Rectangle toRectangle(int width, int height);


    //-------------------------------------------------------------------------
    boolean isNull();

    
    //-------------------------------------------------------------------------
    public int quadrant();
}