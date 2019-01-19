package ao.dd.desktop.model.image;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * User: alex
 * Date: 2-Jun-2010
 * Time: 10:51:47 PM
 */
public class GridUtils
{
    //-------------------------------------------------------------------------
    private GridUtils() {}


    //-------------------------------------------------------------------------
    public static int[][] asMatrix(
            BufferedImage image)
    {
        int[][] toArray = new int[ image.getWidth () ]
                                 [ image.getHeight() ];

        for (int x = 0; x < toArray.length; x++)
        {
            for (int y = 0; y < toArray[x].length; y++)
            {
                toArray[ x ][ y ] = image.getRGB(x, y);
            }
        }

        return toArray;
    }


    //-------------------------------------------------------------------------
    public static Color[][] asMatrix(
            RgbGrid grid, boolean horizontally)
    {
        int w = (  horizontally ? grid.width() : grid.height());
        int h = (! horizontally ? grid.width() : grid.height());

        Color[][] toArray = new Color[ w ][ h ];

        for (int x = 0; x < toArray.length; x++)
        {
            for (int y = 0; y < toArray[x].length; y++)
            {
                if (horizontally)
                {
                    toArray[ x ][ y ] = grid.colour(x, y);
                }
                else
                {
                    toArray[ x ][ y ] = grid.colour(y, x);
                }
            }
        }

        return toArray;
    }
}
