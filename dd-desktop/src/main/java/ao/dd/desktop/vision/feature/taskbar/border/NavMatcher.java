package ao.dd.desktop.vision.feature.taskbar.border;

import ao.dd.desktop.util.ColourMap;
import ao.dd.desktop.vision.feature.line.LinearMatcher;
import ao.util.data.Arrs;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;


/**
 * User: 188952
 * Date: Apr 11, 2010
 * Time: 1:16:08 PM
 */
public class NavMatcher
    extends LinearMatcher
{
    //-LOGGER------------------------------------------------------------------
    public static Logger LOG =
            Logger.getLogger( NavMatcher.class );


    //-CONSTRAINTS-------------------------------------------------------------
    private final double minSmoothness;


    //-------------------------------------------------------------------------
    public NavMatcher(
            int    minClrs,
            int    maxClrs,
            double minClrRatio,
            double maxClrRatio,
            double minPrty,
            int    maxPureColours,
            int    minClrTransitions,
            int    maxClrTransitions,
            double smoothFor)
    {
        super ( minClrs           ,
                maxClrs           ,
                minClrRatio       ,
                maxClrRatio       ,
                minPrty           ,
                maxPureColours    ,
                minClrTransitions ,
                maxClrTransitions );

        minSmoothness = smoothFor;
    }


    //-------------------------------------------------------------------------
    @Override
    public double scoreLine(
            Color[] prevLine,
            Color[] line,
            Color[] nextLine)
    {
        double smoothness = smoothness(line);
        if ( smoothness < minSmoothness ) return 0;

        if (super.scoreLine(
                prevLine, line, nextLine) < 0.00000001) {
            return 0;
        }

        ColourMap colourMap = new ColourMap( line );

        List<Color> colours = colourMap.colours();

        boolean sysTrayBorder_prev =
                hasSysTrayBorder(colours, line, prevLine, colourMap);
        boolean sysTrayBorder_next =
                hasSysTrayBorder(colours, line, nextLine, colourMap);

        LOG.trace(" smoothness: "   + smoothness                +
                  "; sysTray prev/next: "  + sysTrayBorder_prev  +
                                "/"        + sysTrayBorder_next  );

        // todo: remove WORKAROUND
        // todo: systemTray border does not exist in default theme
        if ( colours.size() > 2 &&
             colours.size() < 6 &&
             ! colours.get(0).equals(
                     new Color(49, 104, 213) ))
        {
            if (   sysTrayBorder_next ) return 0;
            if ( ! sysTrayBorder_prev ) return 0;
        }

        if (Arrays.equals(line, nextLine)) return 0;

        // line satisfied all conditions --> PASS
        return 1;
    }

    private boolean hasSysTrayBorder(
            List<Color> sortedColours,
            Color[]     line,
            Color[]     compareWithLine,
            ColourMap   lineColourMap)
    {

        //todo: track sysTray border until it ends.
        
        if ( lineColourMap.purity( 2 ) == 1) return true;

        for( Color c : sortedColours )
        {
            if ( lineColourMap.colours().size() == 1)
            {
                if ( Arrs.indexOf(line           , c ) ==
                     Arrs.indexOf(compareWithLine, c ) )
                {
                     return true;
                }
            }
        }
        return false;
    }


    //-------------------------------------------------------------------------
    private static double smoothness(
            Color[] line)
    {
        if (line        == null ||
            line.length == 0     )
        {
            return 0;
        }

        Color prevColour = line[0];
        for (int i = 1; i < line.length; i++)
        {
            Color colour = line[i];
            if (! prevColour.equals( colour ))
            {
                return (double) i / line.length;
            }

            prevColour = colour;
        }
        return 1;
    }
}