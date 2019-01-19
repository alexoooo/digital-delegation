package ao.dd.desktop.vision.feature.line;

import ao.dd.desktop.util.ColourMap;
import com.google.common.collect.*;
import org.apache.log4j.*;

import java.awt.*;
import java.util.List;

/**
 * User: 188952
 * Date: Apr 22, 2010
 * Time: 9:08:19 PM
 */
public class LinearMatcher
    implements LineMatcher
{
    //-LOGGER------------------------------------------------------------------
    public static Logger LOG =
            Logger.getLogger( LinearMatcher.class );


    //-CONSTRAINTS-------------------------------------------------------------
    private final int    minColours           ;
    private final int    maxColours           ;
    private final double minColourRatio       ;
    private final double maxColourRatio       ;
    private final double minPurity            ;
    private final int    maxPureColours       ;
    private final int    minColourTransitions ;
    private final int    maxColourTransitions ;


    //-------------------------------------------------------------------------
    public LinearMatcher(
            int    minClrs,
            int    maxClrs,
            double minClrRatio,
            double maxClrRatio,
            double minPrty,
            int    maxPureClrs,
            int    minClrTransitions,
            int    maxClrTransitions)
    {
        minColours           = minClrs;
        maxColours           = maxClrs;
        minColourRatio       = minClrRatio;
        maxColourRatio       = maxClrRatio;
        minPurity            = minPrty;
        maxPureColours       = maxPureClrs;
        minColourTransitions = minClrTransitions;
        maxColourTransitions = maxClrTransitions;
    }


    //-------------------------------------------------------------------------
    @Override
    public double scoreLine(
            Color[] prevLine,
            Color[] line,
            Color[] nextLine)
    {
        ColourMap colourMap = new ColourMap( line );

        List<Color> colours =
                Lists.newArrayList( colourMap.colours() );

        double purity      = colourMap.purity      ( maxPureColours );
        double colourRatio = colourMap.colourRatio();
        int    colourTransitions = colourTransitions ( line );
//        double smoothness        = smoothness        ( line );

        if ( colours.size() >= 2 )
        {
            LOG.trace(" color size: "    + colourMap.colours().size() +
                      "; colour Ratio: " + colourRatio               +
                      "; purity: "       + purity                    +
                      "; colour transitions: " + colourTransitions);
        }
        else if ( colours.size() == 1 )
        {
            LOG.trace(" color size: " + colourMap.size());
        }

        // test conditions --> FAIL
        if ( colours.size()        <  minColours           ) return 0;
        if ( colours.size()        >  maxColours           ) return 0;
        if ( colourRatio           <  minColourRatio       ) return 0;
        if ( colourRatio           >  maxColourRatio       ) return 0;
        if ( purity                <  minPurity            ) return 0;
        if ( colourTransitions     <  minColourTransitions ) return 0;
        if ( colourTransitions     >  maxColourTransitions ) return 0;
        if ( colourTransitions     <  0                    ) return 0;

        // line satisfied all conditions --> PASS
        return 1;
    }

    //-------------------------------------------------------------------------
    private int colourTransitions(
            Color[] line)
    {
        if ( line.length == 0 ) return -1;

        int   transitions = 0;
        Color prevColour  = null;

        for (Color c : line)
        {
            if (prevColour != null &&
                    ! c.equals( prevColour ))
            {
                transitions++;
            }
            prevColour = c;
        }
        return transitions;
    }

//    //-------------------------------------------------------------------------
//    public static Color[] toArray(
//            BufferedImage line,
//            Orientation orientation)
//    {
//        int     size          = Math.max(line.getWidth(), line.getHeight());
//        Color[] lineOfColours = new Color[ size ];
//
//        for (int i = 0; i < size; i ++)
//        {
//            lineOfColours[ i ] =
//                    new Color(line.getRGB(
//                            i * orientation.lineDirectionX(),
//                            i * orientation.lineDirectionY()));
//        }
//        return lineOfColours;
//    }


    //-------------------------------------------------------------------------
//    private static double smoothness(
//            Color[] line)
//    {
//        if (line        == null ||
//            line.length == 0     )
//        {
//            return 0;
//        }
//
//        Color prevColour = line[0];
//        for (int i = 1; i < line.length; i++)
//        {
//            Color colour = line[i];
//            if (! prevColour.equals( colour ))
//            {
//                return (double) i / line.length;
//            }
//
//            prevColour = colour;
//        }
//        return 1;
//    }
}
