package ao.dd.desktop.vision.feature.taskbar.border.win7;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.model.area.Areas;
import ao.dd.desktop.model.image.RgbGrid;
import ao.dd.desktop.util.ColourMap;
import ao.dd.desktop.vision.def.Feature;
import ao.dd.desktop.vision.def.Sighting;
import ao.dd.desktop.vision.feature.Screen;
import ao.dd.desktop.vision.feature.line.LineMatcher;
import ao.dd.desktop.vision.feature.taskbar.TaskBarSighting;
import ao.dd.desktop.vision.feature.taskbar.border.NavMatcher;
import ao.dd.desktop.vision.feature.taskbar.border.Orientation;
import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Author: Eugene
 * Date: Apr 3, 2010
 * Time: 1:02:39 AM
 */
public class TaskBarBorderWin7
        implements Feature
{
    //-LOGGER------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger( TaskBarBorderWin7.class );

    private static final double threshold     = 0.95;
    private static final int    maxThickness  =  4;
    private static final int    startOffset   = 25;
    private static final int    ignoreOffset  = 45;

    LineMatcher matcher = new NavMatcher(
            1, 1, Double.NaN, Double.NaN, 1, 1, 0, 0, 1);


    //-------------------------------------------------------------------------
    public TaskBarBorderWin7() {}


    //-------------------------------------------------------------------------
    @Override
    public boolean expectedIn(Sighting sighting)
    {
        return (sighting.feature() instanceof Screen);
    }


    //-------------------------------------------------------------------------
    @Override
    public Iterable<? extends Sighting> locate(
            Sighting inSighting)
    {
        Area in = inSighting.area();
        if (in.display().width () != in.width () ||
            in.display().height() != in.height())
        {
            return Collections.emptyList();
        }

        int maxOffset =
                (Math.max(
                        in.width(),
                        in.height()) - 1
                )/2;

        for (int offset = startOffset; offset < maxOffset; offset++)
        {
            List<Orientation> remainingOrientations =
                    Lists.newArrayList( Orientation.values() );

            Orientation toDelete = null;
            int ignoreFirst;
            for ( Orientation o : remainingOrientations )
            {
                if ( offset > maxOffset)
                {
                    toDelete = o;
                    break;
                }

                Area separator =
                        ( scoreLine(in, offset, o, ignoreOffset) > threshold )
                        ? o.line(in, offset)
                        : Areas.empty();

                if (! separator.isEmpty())
                {
                    Iterable<TaskBarSighting> sighting =
                            buildSighting( in, offset, o, separator, ignoreOffset );

                    boolean hasStartButton =
                            hasStartButton( in, offset, o );

                    LOG.trace("Found Start Button: "+ hasStartButton );

                    if ( hasStartButton )
                    {
                        return sighting;
                    }
                }
            }
            if (toDelete != null)
            {
                remainingOrientations.remove( toDelete );
            }
        }
        return Collections.emptyList();
    }

    private boolean hasStartButton(
            Area        in,
            int         offset,
            Orientation orientation)
    {
        switch (orientation)
        {
            case RIGHT:
            case BOTTOM:
                return new ColourMap(
                        extractRelevantLine( in, offset, orientation, 0))
                        .colours().size() > 10;
            case TOP:
            case LEFT:
                return true;
            default:
                return false;
        }
    }


    //-------------------------------------------------------------------------
    private Iterable<TaskBarSighting> buildSighting(
            Area        in,
            int         offset,
            Orientation orientation,
            Area        separator,
            int         ignoreFirst)
    {
        List<ColourMap> linesColourMaps =
                Lists.newArrayList();

        for (int i = 1; i <= maxThickness; i++)
        {
            ColourMap colourMap =
                    new ColourMap(extractRelevantLine(
                            in, offset + i, orientation, ignoreFirst)) ;

            linesColourMaps.add(colourMap);
        }

        boolean isLocked = isLocked( linesColourMaps );

        if ( isLocked )
        {
            separator = orientation.inwardsInclusive(
                    in, separator, maxThickness);
        }

        LOG.trace("border type: " +
                ((isLocked) ? "locked" : "unlocked"));

        return Arrays.asList(new TaskBarSighting(
                this, separator, orientation, isLocked));
    }

    protected boolean isLocked (
            List<ColourMap> linesColourMaps)
    {
         return ! (
                linesColourMaps.get(0).colours().size() <= 2 &&
                linesColourMaps.get(1).colours().size() <= 2 &&
                linesColourMaps.get(2).colours().size() == 1 &&
                linesColourMaps.get(3).colours().size() == 1 &&
                ! linesColourMaps.get(0).equals(
                        linesColourMaps.get(1))             &&
                ! linesColourMaps.get(0).equals(
                        linesColourMaps.get(2))             );
    }


//    //-------------------------------------------------------------------------
//    private Area recognizeSeparator(
//            Area        in,
//            int         offset,
//            Orientation orientation,
//            int         ignoreFirst)
//    {
//        return ( scoreLine(in, offset, orientation, ignoreFirst) > threshold )
//               ? orientation.line(in, offset)
//               : Areas.empty();
//    }

    private double scoreLine(
            Area        in,
            int         offset,
            Orientation orientation,
            int         ignoreFirstPixels)
    {
//        if ((offset + 1) > ((orientation.isHorizontal()
//                             ? in.height() : in.width())) / 2)
//        {
//            throw new Error("out of screen bounds: " +
//                                in + " offset: " + offset);
//        }

        Color[] prevLine = extractRelevantLine(
                in, offset - 1, orientation, ignoreFirstPixels);

        Color[] currLine = extractRelevantLine(
                in, offset    , orientation, ignoreFirstPixels);

        Color[] nextLine = extractRelevantLine(
                in, offset + 1, orientation, ignoreFirstPixels);

        return matcher.scoreLine(prevLine, currLine, nextLine);
    }


    //-------------------------------------------------------------------------
    private Color[] extractRelevantLine(
            Area        in,
            int         offset,
            Orientation orientation,
            int         ignoreFirst)
    {
        RgbGrid line =
                orientation.line(
                        in, offset).capture();

        try {
            Color[] fullLine =
                    line.asMatrix( ! orientation.isHorizontal() )[0];

            return Arrays.copyOfRange(
                     fullLine, ignoreFirst, fullLine.length);
        }
        catch (Throwable t)
        {
            return null;
        }
    }


    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "Task Bar Border";
    }


    //-------------------------------------------------------------------------
    @Override
    public boolean equals(Object o)
    {
        return getClass() == o.getClass();
    }

    @Override
    public int hashCode()
    {
        return getClass().hashCode();
    }
}