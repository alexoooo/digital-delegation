package ao.dd.desktop.model.image;

import ao.dd.desktop.model.display.Displays;
import ao.dd.desktop.util.Pictures;
import ao.util.io.AoFiles;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import org.apache.log4j.Logger;

/**
 * User: 188952
 * Date: Apr 21, 2010
 * Time: 8:37:47 PM
 */
public class ImageRgbGrid
        implements RgbGrid
{
    //-------------------------------------------------------------------------
    public static void main(String[] args)
    {
        RgbGrid img = new ImageRgbGrid(
                Displays.mainScreen().view().toBufferedImage());

        BufferedImage a = img.toBufferedImage();
        BufferedImage b = img.toBufferedImage();

        System.out.println("a equals b: " +
                coloursEqual(a, b));

        Graphics g = a.getGraphics();
        g.drawRect(10, 20, 30, 40);

        System.out.println("after modifying a, a equals b: " +
                coloursEqual(a, b));
    }


    //-------------------------------------------------------------------------
    private final    BufferedImage  image;
    private final    String         title;
    private volatile int            hashCode = 0;


    //-------------------------------------------------------------------------
    public static Logger LOG =
        Logger.getLogger( ImageRgbGrid.class );


    //-------------------------------------------------------------------------
    public ImageRgbGrid(
            String pathToImage)
    {
        this(new File( pathToImage ));
    }

    public ImageRgbGrid(
            File imageFile)
    {
        this(Pictures.toBufferedImage( imageFile ),
             AoFiles.nameWithoutExtension( imageFile.toString() ));
    }

    public ImageRgbGrid(
            Image image)
    {
        this( image, "" );
    }

    public ImageRgbGrid(
            Image image, String tag)
    {
        this( Pictures.toBufferedImage(image), tag);
    }

    public ImageRgbGrid(
            String fileName, Class relativeTo)
    {
        this ( Pictures.toBufferedImage( fileName, relativeTo),
                relativeTo.toString());
    }

    public ImageRgbGrid(
            BufferedImage copyImage, String tag)
    {
        image = copyImage;
        title = tag;
    }


    //-------------------------------------------------------------------------
    @Override
    public BufferedImage toBufferedImage()
    {
//        return Pictures.toBufferedImage( image );

        BufferedImage clone = new BufferedImage(
                image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g = clone.createGraphics();
        g.drawRenderedImage(image, null);
        g.dispose();
        return clone;
    }


    //-------------------------------------------------------------------------
    @Override
    public int segment(int x, int y)
    {
        return image.getRGB(x, y);
    }

    @Override
    public Color colour(int x, int y)
    {
        return new Color(
                segment(x, y));
    }

    @Override
    public RgbGrid sub(int x, int y, int width, int height)
    {
        return new ImageRgbGrid(
                image.getSubimage(x, y, width, height));
    }

    @Override
    public RgbGrid sub(Rectangle rect)
    {
        return new ImageRgbGrid(
                image.getSubimage(
                        rect.x,
                        rect.y,
                        rect.width,
                        rect.height));
    }


    //-------------------------------------------------------------------------
    @Override
    public int width()
    {
        return image.getWidth();
    }

    @Override
    public int height()
    {
        return image.getHeight();
    }


    //-------------------------------------------------------------------------
    @Override
    public Color[][] asMatrix(boolean horizontally)
    {
        return GridUtils.asMatrix( this, horizontally );
    }


    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return title + " [" + width() + " x " + height() + "]";
    }


    //-------------------------------------------------------------------------
    @Override
    public int hashCode()
    {
        if (hashCode != 0)
        {
            return hashCode;
        }

        int hash = 1;
        for (int x = 0; x < width(); x++)
        {
            for (int y = 0; y < height(); y++)
            {
                hash = 31 * hash + segment(x, y);
            }
        }
        
        hashCode = hash;
        return hash;
    }

    @Override
    public boolean equals(Object o)
    {
        return !(o == null || !(o instanceof RgbGrid)) &&
                coloursEqual(
                        toBufferedImage(),
                        ((RgbGrid) o).toBufferedImage());
    }


    //-------------------------------------------------------------------------
    private static boolean coloursEqual(BufferedImage a, BufferedImage b)
    {
        if (a.getWidth() != b.getWidth() ||
                a.getHeight() != b.getHeight())
        {
            return false;
        }

        for (int x = 0; x < a.getWidth(); x++)
        {
            for (int y = 0; y < a.getHeight(); y++)
            {
                if (a.getRGB(x, y) != b.getRGB(x, y))
                {
                    return false;
                }
            }
        }

        return true;
    }
}
