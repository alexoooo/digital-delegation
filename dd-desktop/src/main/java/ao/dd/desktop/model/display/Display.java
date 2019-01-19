package ao.dd.desktop.model.display;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.model.image.RgbGrid;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;

/**
 * User: 188952
 * Date: Apr 17, 2010
 * Time: 11:26:26 PM
 */
public interface Display
        extends Pointable
{
    //-------------------------------------------------------------------------
    public RgbGrid view();
    public RgbGrid view(Rectangle rectangle);

    public Color      colour(int x, int y);


    //-------------------------------------------------------------------------
    public GraphicsDevice device();


    //-------------------------------------------------------------------------
    public int width();
    public int height();


    //-------------------------------------------------------------------------
    public Area      asArea();
    public Rectangle asRectangle();
}
