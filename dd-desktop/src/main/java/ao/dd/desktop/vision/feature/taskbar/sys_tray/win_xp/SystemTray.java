package ao.dd.desktop.vision.feature.taskbar.sys_tray.win_xp;

import ao.dd.desktop.model.area.Areas;
import ao.dd.desktop.model.pixel.Pixel;
import ao.dd.desktop.model.pixel.Pixels;
import ao.dd.desktop.vision.def.Feature;
import ao.dd.desktop.vision.def.Sighting;
import ao.dd.desktop.vision.feature.line.LineMatcher;
import ao.dd.desktop.vision.feature.line.LinearMatcher;
import ao.dd.desktop.vision.feature.taskbar.TaskBar;
import ao.dd.desktop.vision.feature.taskbar.border.OrientedSighting;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;

/**
 * User: 188952
 * Date: Apr 21, 2010
 * Time: 12:41:08 AM
 */
public class SystemTray
    implements Feature
{
    //-------------------------------------------------------------------------
    public static Logger LOG =
            Logger.getLogger( SystemTray.class );


    //-------------------------------------------------------------------------
    private static final LineMatcher matcher =
            new LinearMatcher(
                    5,  10, 0.01, 0.6, 0.40,  1, 13, 16);

    //-------------------------------------------------------------------------
    @Override
    public boolean expectedIn(Sighting sighting)
    {
        return (sighting.feature() instanceof TaskBar);
    }


    //-------------------------------------------------------------------------
    @Override
    public Iterable<OrientedSighting> locate(
            Sighting inSighting)
    {
        OrientedSighting in = (OrientedSighting) inSighting;

        Color[][] lines =
                in.area().capture().asMatrix(
                        in.orientation().isHorizontal() );

        for (int i = lines.length - 2; i >= 1; i--)
        {
            Color[] prevLine = lines[i - 1];
            Color[]     line = lines[  i  ];
            Color[] nextLine = lines[i + 1];

            LOG.trace("searching... line " + i );

            if ( matcher.scoreLine(prevLine, line, nextLine) == 1 )
            {
                Pixel start, end;
                if (in.orientation().isHorizontal())
                {
                    start = Pixels.newInstance(
                            in.area().display(),
                            i,
                            in.area().bottomRight().y());

                    end   = in.area().topRight();
                }
                else
                {
                    start = Pixels.newInstance(
                            inSighting.area().display(),
                            inSighting.area().topLeft().x(), i);

                    end   = in.area().bottomRight();
                }

                return Arrays.asList(
                        new OrientedSighting(this,
                                Areas.surroundingArea(
                                        start, end),
                                in.orientation()));
            }
        }
        return Collections.emptyList();
    }


    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "System Tray";
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
