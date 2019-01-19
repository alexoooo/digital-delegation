package ao.dd.desktop.vision.locate;

import ao.dd.desktop.model.image.SegmentGrid;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.List;

/**
 * User: alex
 * Date: 19-May-2010
 * Time: 8:20:47 PM
 */
public class SimpleLocator
        extends AbstractLocator
{
    //-------------------------------------------------------------------------
    private static Logger LOG =
            Logger.getLogger( SimpleLocator.class );

    
    //-------------------------------------------------------------------------
    @Override
    public List<Rectangle> locate(SegmentGrid in, SegmentGrid target)
    {
        List<Rectangle> locations = Lists.newArrayList();

        for (int x = 0; x < in.width(); x++)
        {
            for (int y = 0; y < in.height(); y++)
            {   
                if (matches(in, target, x, y))
                {
//                    LOG.trace("FOUND "+ (locations.size()+1) +
//                            " match at ("+x+","+y+")");
                    locations.add(new Rectangle(
                            x, y, target.width(), target.height()));
                }
            }
        }

        return locations;
    }


    //-------------------------------------------------------------------------
    @Override
    public Rectangle difference(
            SegmentGrid grid1,
            SegmentGrid grid2)
    {
        if (grid1 == null ||
            grid2 == null ||
            grid1.width() != grid2.width() ||
            grid1.height() != grid2.height())
        {
            return null;
        }

        int top = Integer.MAX_VALUE;
        int left = Integer.MAX_VALUE;
        int right = -1;
        int bottom = -1;

        for (int x = 0 ; x < grid1.width(); x ++)
        {
            for (int y = 0 ; y < grid1.height(); y ++)
            {
                if (grid1.segment(x,y) != grid2.segment(x,y))
                {
                    if ( x < left )   { left   = x; }
                    if ( y < top )    { top    = y; }
                    if ( x > right )  { right  = x; }
                    if ( y > bottom ) { bottom = y; }
                }
            }
        }

        return (right == -1)
                ? null
                : new Rectangle(
                        left,
                        top,
                        right  - left,
                        bottom - top);
    }


    //-------------------------------------------------------------------------
    private boolean matches(
            SegmentGrid in,
            SegmentGrid target,
            int         deltaX,
            int         deltaY)
    {
        if ((deltaX + target.width ()) > in.width () ||
            (deltaY + target.height()) > in.height())
        {
            return false;
        }

        for (int x = 0; x < target.width(); x++)
        {
            for (int y = 0; y < target.height(); y++)
            {
                int inSegment     = in.segment(
                        deltaX + x, deltaY + y);

                int targetSegment = target.segment(x, y);

//                if ( deltaX > 1070 &&
//                     deltaX < 1170 &&
//                     deltaY > 50   &&
//                     deltaY < 80    )
//                {
//                    LOG.trace( "delta at "+
//                        (deltaX + x)+ "," +(deltaY + y)+
//                        " is " + (inSegment - targetSegment));
//                    LOG.trace( "match at "+
//                        (deltaX + x)+ "," +(deltaY + y) +
//                        " is "   + new Color(inSegment) +
//                        ", and " + new Color(targetSegment));
//                }

                if (inSegment != targetSegment)
                {
                    return false;
                }
            }
        }
        return true;
    }
}
