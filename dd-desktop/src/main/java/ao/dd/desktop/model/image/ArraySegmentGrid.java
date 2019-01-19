package ao.dd.desktop.model.image;

import org.apache.log4j.Logger;

import java.awt.*;

/**
 * User: alex
 * Date: 2-Jun-2010
 * Time: 8:44:25 PM
 */
public class ArraySegmentGrid
        implements SegmentGrid
{
    //-------------------------------------------------------------------------
    public static Logger LOG =
        Logger.getLogger( ArrayRgbGrid.class );


    //-------------------------------------------------------------------------
    private final int     xOffset;
    private final int     yOffset;
    private final int     width;
    private final int     height;

    private final int[][] segments;

    private final    String title;
    private volatile int    hashCode = 0;


    //-------------------------------------------------------------------------
    protected ArraySegmentGrid(
            ArraySegmentGrid copyGrid)
    {
        this(copyGrid.segments,
             copyGrid.xOffset,
             copyGrid.yOffset,
             copyGrid.width,
             copyGrid.height,
             copyGrid.title,
             copyGrid.hashCode);
    }

//    protected ArraySegmentGrid(
//            int[][] copyImage,
//            String  name)
//    {
//        this(copyImage,
//             0,
//             0,
//             copyImage.length,
//             copyImage[0].length,
//             name,
//             0);
//    }

    protected ArraySegmentGrid(
            int[][] copyImage,
            int     copyOffsetX,
            int     copyOffsetY,
            int     copyWidth,
            int     copyHeight,
            String  name)
    {
        this(copyImage, copyOffsetX, copyOffsetY,
             copyWidth, copyHeight, name, 0);
    }

    private ArraySegmentGrid(
            int[][] copyImage,
            int     copyOffsetX,
            int     copyOffsetY,
            int     copyWidth,
            int     copyHeight,
            String  name,
            int     copyHashCode)
    {
        title    = name;
        width    = copyWidth;
        height   = copyHeight;
        xOffset  = copyOffsetX;
        yOffset  = copyOffsetY;
        segments = copyImage;
        hashCode = copyHashCode;
    }


    //-------------------------------------------------------------------------
    @Override
    public int segment(int x, int y)
    {
        return segments[ xOffset + x ]
                       [ yOffset + y ];
    }


    //-------------------------------------------------------------------------
    @Override
    public int width()
    {
        return width;
    }

    @Override
    public int height()
    {
        return height;
    }


    //-------------------------------------------------------------------------
    @Override
    public ArraySegmentGrid sub(
            int x, int y, int width, int height)
    {
            return new ArraySegmentGrid(
                segments,
                xOffset + x,
                yOffset + y,
                width,
                height,
                title);
    }

    @Override
    public ArraySegmentGrid sub(
            Rectangle rect)
    {
        return new ArraySegmentGrid(
                segments,
                xOffset + rect.x,
                yOffset + rect.y,
                rect.width,
                rect.height,
                title);
    }


    //-------------------------------------------------------------------------
    // Note that:
    //   ! Arrays.equals(
    //        Ints.concat( asMatrix() )
    //

    public int[][] asMatrix()
    {
        int[][] matrix = new int[ width ][ height ];

        for (int x = xOffset, i = 0; i < width; x++, i++)
        {
            for (int y = yOffset, j = 0; j < height; y++, j++)
            {
                matrix[ x ][ y ] = segments[ x ][ y ];
            }
        }

        return matrix;
    }

    public int[] flatten()
    {
        int[] flat = new int[ width * height ];
        flatten(flat);
        return flat;
    }
    public void flatten(int[] into)
    {
        int index = 0;
        for (int y = yOffset, j = 0; j < height; y++, j++)
        {
            for (int x = xOffset, i = 0; i < width; x++, i++)
            {
                into[ index++ ] = segments[ x ][ y ];
            }
        }
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
        for (int x = xOffset + width - 1; x >= xOffset; x--)
        {
            for (int y = yOffset + height - 1; y >= yOffset; y--)
            {
                hash = 31 * hash + segments[ x ][ y ];
            }
        }

        hashCode = hash;
        return hash;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof SegmentGrid))
        {
            return false;
        }

        SegmentGrid that = (SegmentGrid) o;

        if (width != that.width() ||
                height != that.height())
        {
            return false;
        }

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                if (segment(x, y) != that.segment(x, y))
                {
                    return false;
                }
            }
        }

        return true;
    }
}
