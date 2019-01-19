package ao.dd.desktop.vision.feature.taskbar;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.vision.def.Feature;
import ao.dd.desktop.vision.def.Sighting;
import ao.dd.desktop.vision.feature.Screen;
import ao.dd.desktop.vision.feature.taskbar.border.Orientation;
import ao.dd.desktop.vision.feature.taskbar.border.OrientedSighting;
import ao.dd.desktop.vision.feature.taskbar.border.TaskBarBorders;

import java.util.Arrays;
import java.util.Collections;

/**
 * User: 188952
 * Date: Apr 1, 2010
 * Time: 12:05:55 PM
 */
public class TaskBar
        implements Feature
{
    //-------------------------------------------------------------------------
    public TaskBar() {}


    //-------------------------------------------------------------------------
    @Override
    public boolean expectedIn(Sighting sighting)
    {
        return (sighting.feature() instanceof Screen);
    }


    //-------------------------------------------------------------------------
    @Override
    public Iterable<OrientedSighting> locate(
            Sighting in)
    {
        TaskBarSighting borderSighting = TaskBarBorders.locate( in );

        if ( borderSighting == null )
        {
            return Collections.emptyList();
        }

        Orientation orientation = borderSighting.orientation();
        Area        outwards    =
                orientation.outwards(
                        in.area(), borderSighting.area());
                
        return outwards.isEmpty()
               ? Collections.<OrientedSighting>emptyList()
               : Arrays.asList(
                    new OrientedSighting(
                            this, outwards, orientation));
    }


    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "Task Bar";
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