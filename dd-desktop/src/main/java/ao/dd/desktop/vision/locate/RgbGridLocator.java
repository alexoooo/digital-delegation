package ao.dd.desktop.vision.locate;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.model.area.Areas;
import ao.dd.desktop.model.image.RgbGrid;
import ao.dd.desktop.model.image.SegmentGrid;
import ao.dd.desktop.util.ColourMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * User: aostrovsky
 * User: y.malikov
 *
 * Created:
 *   Date: 26-May-2009
 *   Time: 3:17:48 PM
 */
public class RgbGridLocator
        implements Locator
{
    //-------------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger( RgbGridLocator.class );


    //-------------------------------------------------------------------------
    @Override
    public List<Area> locate(Area inArea, SegmentGrid target)
    {
        List<Area> locations = new ArrayList<Area>();
        for (Rectangle location : locate(
                inArea.capture(), target))
        {
            locations.add(Areas.newInstance(
                    inArea.display(), location));
        }
        return locations;
    }

    @Override
    public List<Rectangle> locate(SegmentGrid in, SegmentGrid target)
    {
        List<Point> topLeft = identicalMatches(in, target);

        return inflate(
                topLeft,
                target.width(),
                target.height());
    }

    @Override
    public Rectangle difference(SegmentGrid grid1, SegmentGrid grid2) {
        throw new UnsupportedOperationException();
    }


    //-------------------------------------------------------------------------
    private static List<Rectangle> inflate(
            List<Point> topLeftCorners,
            int         width,
            int         height)
    {
        List<Rectangle> rectangles = Lists.newArrayList();

        for (Point topLeftCorner : topLeftCorners)
        {
            rectangles.add(new Rectangle(
                    topLeftCorner.x,
                    topLeftCorner.y,
                    width,
                    height));
        }

        return rectangles;
    }


    //-------------------------------------------------------------------------
    private List<Point> identicalMatches(
            SegmentGrid onSG,
            SegmentGrid targetSG)

    {
        RgbGrid target = (RgbGrid) targetSG;
        RgbGrid on     = (RgbGrid) onSG;

        if (target == null) throw new Error("Target image is null.");

        ColourMap targetColourMap = new ColourMap( target );

        // todo: need to choose the first point more effectively
        // todo: currently choosing top left by default
        List<Point> candidates =
                initialCandidates( on, target, targetColourMap);

        next_colour_cycle:
        while (! targetColourMap.isEmpty())
        {
            Map<Color, Point> toRemove = Maps.newHashMap();

            for (Color c : targetColourMap.colours())
            {
                if (candidates.size() <= 1) {
                    break next_colour_cycle;
                }

                for ( Point p : targetColourMap.points( c ))
                {
                    candidates = filterCandidates(
                            on, candidates, p, c.getRGB());

                    toRemove.put(c, p);
                }
            }

            for (Map.Entry<Color, Point>
                    pointToRemove : toRemove.entrySet())
            {
                targetColourMap.remove(
                        pointToRemove.getKey(),
                        pointToRemove.getValue());
            }
        }
        return candidates;
    }


    //-------------------------------------------------------------------------
    private List<Point> filterCandidates(
            RgbGrid on,
            List<Point> candidates,
            Point       targetOffset,
            int         targetColour)
    {
//        LOG.trace("Filtering " + candidates.size() + " candidates" +
//                       " for " + targetOffset + " -> " + targetColour);

        List<Point> nextCandidates = Lists.newArrayList();

        for (Point c : candidates)
        {
            if (targetColour == on.segment(
                    c.x + targetOffset.x,
                    c.y + targetOffset.y) )
            {
                nextCandidates.add( c );
            }
        }

        return nextCandidates;
    }


    //-------------------------------------------------------------------------
    private List<Point> initialCandidates(
            RgbGrid on,
            RgbGrid target,
            ColourMap  targetColourMap)
    {
        List<Point> topLeftOfInitialCandidates = Lists.newArrayList();

        Point targetOffset =
                anOffsetOfLeastFrequentColour( targetColourMap );

        if (targetOffset == null)
        {
            return Collections.emptyList();
        }

        int targetColour =
                target.colour(
                        targetOffset.x,
                        targetOffset.y
                ).getRGB();

        for (int x = on.width() - target.width(); x >= 0; x--)
        {
            for (int y = on.height() - target.height(); y >= 0; y--)
            {

                if (on.segment(
                        x + targetOffset.x,
                        y + targetOffset.y) == targetColour)
                {
                    topLeftOfInitialCandidates.add(
                            new Point( x, y ));
                }
            }
        }

        return topLeftOfInitialCandidates;
    }


    private Point anOffsetOfLeastFrequentColour(
            ColourMap targetColourMap)
    {
        Point targetOffset = null;
        int   minCount     = Integer.MAX_VALUE;

        Color leastFrequent = targetColourMap.leastFrequent();
        for (Color c : targetColourMap.colours() )
        {
            Collection<Point> points = targetColourMap.points( c );

            if (minCount > points.size())
            {
                minCount     = points.size();
                targetOffset = points.iterator().next();
            }
        }

        LOG.trace("Filtering for " + targetOffset +
                          " with " + minCount     + " pixels.");

        return targetOffset;
    }


    //-------------------------------------------------------------------------
//    @Override
//    public Area locate(
//                String imageFileName)
//    {
//        BufferedImage image = toBufferedImage(imageFileName);
//        if (image == null)
//        {
//            LOG.error("can't get image from file" + imageFileName);
//            return Areas.empty();
//        }
//        return locate(image);
//    }


//    @Override
//    public List<Pixel> locate(
//            Color color)
//    {
//        List<Pixel> matching = Lists.newArrayList();
//        if (color == null) return matching;
//
//        Stopwatch timer = new Stopwatch();
//
//        for (int x = 0; x < onImage.getWidth() ; x++)
//        {
//            for (int y = 0; y < onImage.getHeight(); y++)
//            {
//                Pixel test = Pixels.surroundingArea(x , y);
//                Color test2 = test.colourOn(onImage);
//                if (test.colourOn(onImage)
//                        .equals(color))
//                {
//                    matching.add(test);
//                }
//            }
//        }
//
//        LOG.debug("found Color : " + Iterables.size( matching ) +
//                    " exact matches in "+ timer);
//
//        return matching;
//    }
}
