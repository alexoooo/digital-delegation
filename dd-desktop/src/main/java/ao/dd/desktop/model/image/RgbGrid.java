package ao.dd.desktop.model.image;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: 188952
 * Date: Apr 21, 2010
 * Time: 8:35:36 PM
 */
public interface RgbGrid
        extends SegmentGrid
{
    //-------------------------------------------------------------------------
    public BufferedImage toBufferedImage();


    //-------------------------------------------------------------------------
//    public int   rgb   (int x, int y);
    public Color colour(int x, int y);


//    //-------------------------------------------------------------------------
//    public int width ();
//    public int height();


    //-------------------------------------------------------------------------
//    public RgbGrid subGrid(
//            int x, int y, int width, int height);

    @Override
    public RgbGrid sub(
            int x, int y, int width, int height);

    public RgbGrid sub(Rectangle rectangle);


    //-------------------------------------------------------------------------
    /**
     * Computes a Color matrix in one of two orientations:
     *    horizontally: row by row
     *  ! horizontally: column by column
     *
     * @param horizontally by row (as opposed to by column)
     * @return aligned colour matrix
     */
    public Color[][] asMatrix( boolean horizontally );
}
