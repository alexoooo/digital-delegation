package ao.dd.desktop.model.image;

import ao.dd.desktop.util.Pictures;
import ao.util.io.AoFiles;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;

/**
 * User: alex
 * Date: 2-Jun-2010
 * Time: 10:05:51 PM
 */
public class ArrayRgbGrid
        extends    ArraySegmentGrid
        implements RgbGrid
{

    //-------------------------------------------------------------------------
    public ArrayRgbGrid(
            String pathToImage)
    {
        this(new File( pathToImage ));
    }

    public ArrayRgbGrid(
            File imageFile)
    {
        this(Pictures.toBufferedImage( imageFile ),
             AoFiles.nameWithoutExtension( imageFile.toString() ));
    }

    public ArrayRgbGrid(
            Image image)
    {
        this( image, "" );
    }

    public ArrayRgbGrid(
            Image image, String tag)
    {
        this( Pictures.toBufferedImage(image), tag);
    }
    
    public ArrayRgbGrid(
            String resourcePath, Class relativeTo)
    {
        this ( Pictures.toBufferedImage(resourcePath, relativeTo),
                relativeTo.toString());
    }

    public ArrayRgbGrid(
            BufferedImage protoImage, String name)
    {
        super(GridUtils.asMatrix( protoImage ),
              0, 0, protoImage.getWidth(), protoImage.getHeight(), name);
    }


    //-------------------------------------------------------------------------
    protected ArrayRgbGrid(
            ArraySegmentGrid copyGrid)
    {
        super(copyGrid);
    }


    //-------------------------------------------------------------------------
    @Override
    public Color colour(int x, int y)
    {
        return new Color(
                segment(x, y));
    }


    //-------------------------------------------------------------------------
    @Override
    public ArrayRgbGrid sub(
            int x, int y, int width, int height)
    {
        return new ArrayRgbGrid(
                super.sub(x, y, width, height));
    }

    @Override
    public ArrayRgbGrid sub(Rectangle rect)
    {
        return ( rect == null )
                ? null
                : new ArrayRgbGrid(
                  sub(rect.x, rect.y, rect.width, rect.height));
    }


    //-------------------------------------------------------------------------
    @Override
    public Color[][] asMatrix(boolean horizontally)
    {
        return GridUtils.asMatrix( this, horizontally );
    }


    //-------------------------------------------------------------------------
    @Override
    public BufferedImage toBufferedImage()
    {
        BufferedImage image = new BufferedImage(
                width(), height(), BufferedImage.TYPE_INT_RGB);

        int[] pixels = ((DataBufferInt) image.getRaster()
                .getDataBuffer()).getData();

        flatten( pixels );
//        System.arraycopy(flatten(), 0, pixels, 0, pixels.length);

        return image;
    }
}
