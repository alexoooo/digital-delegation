package ao.dd.desktop.vision.locate;

import ao.dd.desktop.model.image.SegmentGrid;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Date: Aug 17, 2010
 * Time: 9:04:43 PM
 *
 * See: http://en.wikipedia.org/wiki/
 *        Boyer%E2%80%93Moore%E2%80%93Horspool_algorithm
 */
public class BoyerMooreHorspoolLocator
        extends AbstractLocator
{
    //------------------------------------------------------------------------
    @Override
    public List<Rectangle> locate(
            SegmentGrid in, SegmentGrid target)
    {
        int targetRowIndex = (target.height() / 2);

        Map<Integer, Integer> rightMostIndexes =
                preProcessForBadSegmentShift(
                        target, targetRowIndex);

        List<Point> rowMatches = Lists.newArrayList();

        for (int lookInRow = (in.height() - targetRowIndex) - 1;
                 lookInRow >= targetRowIndex;
                 lookInRow--)
        {
            findInRow(in, lookInRow,
                    target, targetRowIndex, rightMostIndexes,
                    rowMatches);
        }

        if (rowMatches.isEmpty())
        {
            return Collections.emptyList();
        }

        List<Rectangle> fullMatches = Lists.newArrayList();
        for (Point rowMatch : rowMatches)
        {
            int xOffset = rowMatch.x;
            int yOffset = rowMatch.y - targetRowIndex;

            if (naiveMatch(
                    in, target,
                    xOffset, yOffset))
            {
                fullMatches.add(new Rectangle(
                        xOffset, yOffset,
                        target.width(),
                        target.height()));
            }
        }
        return fullMatches;
    }


    //------------------------------------------------------------------------
    private boolean naiveMatch(
            SegmentGrid in, SegmentGrid target,
            int xOffset, int yOffset)
    {
        for (int x = 0, inX = xOffset;
                 x < target.width(); x++, inX++)
        {
            for (int y = 0, inY = yOffset;
                     y < target.height(); y++, inY++)
            {
                if (in.segment(inX, inY) !=
                        target.segment(x, y))
                {
                    return false;
                }
            }
        }

        return true;
    }


    //------------------------------------------------------------------------
    private void findInRow(
            SegmentGrid           lookIn,
            int                   lookInRow,
            SegmentGrid           lookFor,
            int                   lookForRow,
            Map<Integer, Integer> rightMostIndexes,
            List<Point>           matches)
    {
        int m = lookIn    .width();
        int n = lookFor.width();

        int alignedAt = 0;
        while (alignedAt + (n - 1) < m)
        {
            for (int indexInPattern = n - 1;
                     indexInPattern >= 0;
                     indexInPattern--)
            {
                int indexInSource = alignedAt + indexInPattern;

                if (indexInSource >= m)
                {
                    break;
                }

                int a = lookIn .segment(indexInSource , lookInRow  );
                int b = lookFor.segment(indexInPattern, lookForRow );

                if (a != b)
                {
                    Integer r = rightMostIndexes.get( a );
                    if (r == null)
                    {
                        alignedAt = indexInSource + 1;
                    }
                    else
                    {
                        int shift = indexInSource - (alignedAt + r);
                        alignedAt += (shift > 0 ? shift : 1);
                    }

                    break;
                }
                else if (indexInPattern == 0)
                {
                    matches.add(new Point(
                            alignedAt, lookInRow));
                    alignedAt++;
                }
            }
        }
    }

    private Map<Integer, Integer> preProcessForBadSegmentShift(
            SegmentGrid target, int targetRowIndex)
    {
        Map<Integer, Integer> shifts = Maps.newHashMap();

        for (int i = target.width() - 1; i >= 0; i--)
        {
            Integer segment = target.segment(
                    i, targetRowIndex);

            if (! shifts.containsKey( segment ))
            {
                shifts.put(segment, i);
            }
        }

        return shifts;
    }


    //------------------------------------------------------------------------
    @Override
    public Rectangle difference(
            SegmentGrid grid1, SegmentGrid grid2)
    {
        throw new UnsupportedOperationException();
    }
}
