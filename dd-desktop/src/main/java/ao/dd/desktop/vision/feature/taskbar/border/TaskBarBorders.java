package ao.dd.desktop.vision.feature.taskbar.border;

import ao.dd.desktop.vision.def.Feature;
import ao.dd.desktop.vision.def.Sighting;
import ao.dd.desktop.vision.feature.taskbar.TaskBarSighting;
import ao.dd.desktop.vision.feature.taskbar.border.win7.TaskBarBorderWin7;
import ao.dd.desktop.vision.feature.taskbar.border.win_xp.EdgeTaskBarBorder;
import ao.dd.desktop.vision.feature.taskbar.border.win_xp.TaskBarBorderWinXP;
import ao.dd.desktop.vision.impl.FeatureCache;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: 188952
 * Date: Apr 26, 2010
 * Time: 1:10:36 AM
 */
public class TaskBarBorders
{
    //------------------------------------------------------------------------
    private static final TaskBarBorderWinXP winXP_Border =
            new TaskBarBorderWinXP();

    private static final TaskBarBorderWin7 win7_Border  =
            new TaskBarBorderWin7();

//    private static final TaskBarBorderWinXP closedBorder =
//            new EdgeTaskBarBorder();


    //------------------------------------------------------------------------
    public TaskBarBorders() {}


    //------------------------------------------------------------------------
    public static Iterable<Feature> all()
    {
        Collection<Feature> borders = Collections.emptyList();
        if ( win7_Border  != null) borders.add ( new TaskBarBorderWin7()  );
        if ( winXP_Border != null) borders.add ( winXP_Border );

        return borders;
    }

    public static TaskBarSighting locate(
            Sighting in)
    {
        Feature border = null;
        for (Feature f : all())
        {
            if (f != null) border = f;
        }

        return locate( in, border );
    }

    public static TaskBarSighting locate(
            Sighting in, Feature border)
    {
        Iterable<? extends Sighting> location =
                FeatureCache.locate(
                        border, in);
        return (location != null)
                ? (TaskBarSighting)
                  (Iterables.getOnlyElement( location, null) )
                : null;
    }
}