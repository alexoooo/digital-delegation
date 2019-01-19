package ao.dd.desktop.vision.feature.taskbar.border.win_xp;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.vision.def.Feature;
import ao.dd.desktop.vision.def.Sighting;
import ao.dd.desktop.vision.feature.Screen;

/**
 * User: 188952
 * Date: Apr 25, 2010
 * Time: 11:43:07 PM
 */
public class EdgeTaskBarBorder 
        extends TaskBarBorderWinXP
        implements Feature
{
    //-------------------------------------------------------------------------
    @Override
    public boolean expectedIn(Sighting featureSighting)
    {
        return (featureSighting.feature() instanceof Screen);
    }


    //-------------------------------------------------------------------------
    @Override
    public Iterable<? extends Sighting> locate(
            Sighting inSighting)
    {
        Area in = inSighting.area();
        return null;
//        return match(
//                0,
//                in,
//                Lists.newArrayList(
//                        Orientation.values()));
    }


    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "Edge Task Bar Border";
    }
}
