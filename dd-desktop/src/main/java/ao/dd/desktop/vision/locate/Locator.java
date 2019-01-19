package ao.dd.desktop.vision.locate;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.model.image.SegmentGrid;

import java.awt.*;
import java.util.List;

/**
 * User: 188952
 * Date: Apr 2, 2010
 * Time: 9:31:14 PM
 */
public interface Locator
{
    //-------------------------------------------------------------------------
//    Area locate(String imageFileName);
//
//    Area locate(BufferedImage target);
//

//    public List<Rectangle> locate(
//            BufferedImage in,
//            BufferedImage target);

//    public List<Point> locate(Color color);


    //-------------------------------------------------------------------------
    public List<Area> locate(
            Area inArea, SegmentGrid target);

    /**
     * Locates zero or more non-overlapping occurrences of
     *  "target" within "in".
     *
     * @param in where to locate in
     * @param target exact image to look for within "in"
     * @return non-nesting occurrences of "target" within "in"
     */
    public List<Rectangle> locate(
            SegmentGrid in,
            SegmentGrid target);


    //-------------------------------------------------------------------------
    Rectangle difference(
            SegmentGrid grid1,
            SegmentGrid grid2);

//    public List<Rectangle> difference(
//            BufferedImage a,
//            BufferedImage b);

//    public List<Rectangle> difference(
//            ColourMap a,
//            ColourMap b);
}
