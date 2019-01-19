package ao.dd.desktop.vision.feature.taskbar.date_time.win_xp;

import ao.dd.desktop.model.area.Areas;
import ao.dd.desktop.model.pixel.Pixel;
import ao.dd.desktop.model.pixel.Pixels;
import ao.dd.desktop.vision.def.Feature;
import ao.dd.desktop.vision.def.Sighting;
import ao.dd.desktop.vision.feature.line.LinearMatcher;
import ao.dd.desktop.vision.feature.taskbar.border.OrientedSighting;
import ao.dd.desktop.vision.impl.SimpleSighting;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.Arrays;

/**
 * User: 188952
 * Date: Apr 23, 2010
 * Time: 12:41:46 AM
 */
public class DateTimeNotification
    implements Feature
{
    //-------------------------------------------------------------------------
    public static Logger LOG =
            Logger.getLogger( DateTimeNotification.class );


    //-------------------------------------------------------------------------
    @Override
    public boolean expectedIn(Sighting sighting)
    {
        return (sighting.feature() instanceof ao.dd.desktop.vision.feature.taskbar.sys_tray.win_xp.SystemTray);
    }


    //-------------------------------------------------------------------------
    @Override
    public Iterable<? extends Sighting> locate(
            Sighting inSighting)
    {
        OrientedSighting in = (OrientedSighting) inSighting;

        LinearMatcher matcher = new LinearMatcher(
                20, 23, 0.1, 0.9, 0.2, 2, 35, 50);

        Color[][] lines =
                in.area().capture().asMatrix(
                        in.orientation().isHorizontal());

        Pixel start = null, end = null;
        for (int i = lines.length - 2; i >= 1; i--)
        {
            Color[] prevLine = lines[i - 1];
            Color[]     line = lines[  i  ];
            Color[] nextLine = lines[i + 1];

            double lineScore = matcher.scoreLine(prevLine, line, nextLine);
            LOG.trace("line " + i + " scored: " + lineScore );

            if ( lineScore == 1 )
            {
                LOG.trace("FOUND at line " + i );

                if (in.orientation().isHorizontal())
                {
                    start = Pixels.newInstance(
                            in.area().display(),
                            in.area().bottomLeft().x() + i,
                            in.area().bottomRight().y());

                    end   = in.area().topRight();
                }
                else
                {
                    start = Pixels.newInstance(
                            inSighting.area().display(),
                            inSighting.area().topLeft().x(),
                            in.area().topLeft().y() + i);

                    end   = in.area().bottomRight();
                }
            }
        }
        return Arrays.asList(
                            new SimpleSighting(this,
                                    Areas.surroundingArea(
                                            start, end)));

//        return Collections.emptyList();
    }

    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "Date Time";
    }
}
