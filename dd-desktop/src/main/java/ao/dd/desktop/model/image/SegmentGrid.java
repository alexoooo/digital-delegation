package ao.dd.desktop.model.image;

import java.awt.*;

/**
 * User: alex
 * Date: 19-May-2010
 * Time: 8:15:32 PM
 *
 * top left is (0, 0),
 */
public interface SegmentGrid
{
    //-------------------------------------------------------------------------
    public int width ();
    public int height();


    //-------------------------------------------------------------------------
    public int segment(int x, int y);
    

    //-------------------------------------------------------------------------
    public SegmentGrid sub(
            int x, int y, int width, int height);

    public SegmentGrid sub(
            Rectangle rect);
}
