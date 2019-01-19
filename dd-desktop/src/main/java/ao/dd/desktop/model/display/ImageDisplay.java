package ao.dd.desktop.model.display;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.model.area.Areas;
import ao.dd.desktop.model.image.ArrayRgbGrid;
import ao.dd.desktop.model.image.RgbGrid;
import ao.dd.desktop.util.Pictures;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * User: 188952
 * Date: Apr 17, 2010
 * Time: 11:41:15 PM
 */
public class ImageDisplay
        implements Display
{
    //-------------------------------------------------------------------------
    private final RgbGrid view;
    private final String     name;
    private final Pointable  listener;


    //-------------------------------------------------------------------------
    public ImageDisplay(
            String filePath)
    {
        this(Pictures.toBufferedImage( filePath ),
             filePath);
    }

    public ImageDisplay(
            BufferedImage fromImage)
    {
        this(fromImage, fromImage.toString());
    }

    public ImageDisplay(
            RgbGrid fromImage)
    {
        this(new Dummy(), fromImage, fromImage.toString());
    }

    public ImageDisplay(
            BufferedImage fromImage,
            String        imageName)
    {
        this(new Dummy(), new ArrayRgbGrid(fromImage), imageName);
    }

    public ImageDisplay(
            Pointable  pointable,
            RgbGrid fromImage,
            String     imageName)
    {
        view     = fromImage;
        name     = imageName;
        listener = pointable;
    }


    //-------------------------------------------------------------------------
    @Override
    public RgbGrid view()
    {
        return view;
    }

    @Override
    public RgbGrid view(Rectangle rectangle)
    {
        return view.sub(
                rectangle.x,
                rectangle.y,
                rectangle.width,
                rectangle.height);
    }

    @Override
    public Color colour(int x, int y)
    {
        return new Color(
                view.segment(x, y));
    }


    //-------------------------------------------------------------------------
    @Override
    public boolean point(int x, int y)
    {
        return listener.point(x, y);
    }

    public Pointable pointable()
    {
        return listener;
    }


    //-------------------------------------------------------------------------
    @Override
    public GraphicsDevice device()
    {
        return null;
    }


    //-------------------------------------------------------------------------
    @Override
    public int width()
    {
        return view.width();
    }

    @Override
    public int height()
    {
        return view.height();
    }


    //-------------------------------------------------------------------------
    @Override
    public Area asArea()
    {
        return Areas.newInstance(
                 this, asRectangle());
    }

    @Override
    public Rectangle asRectangle()
    {
        return new Rectangle(
                 0, 0, width(), height());
    }


    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return name;
    }
}
