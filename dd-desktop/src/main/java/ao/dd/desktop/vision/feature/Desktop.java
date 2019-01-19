package ao.dd.desktop.vision.feature;

import ao.dd.desktop.vision.def.Feature;
import ao.dd.desktop.vision.def.Sighting;
import ao.dd.desktop.vision.feature.taskbar.TaskBarSighting;
import ao.dd.desktop.vision.feature.taskbar.border.TaskBarBorders;
import ao.dd.desktop.vision.impl.SimpleSighting;

import java.util.Arrays;

/**
 * User: 188952
 * Date: Apr 18, 2010
 * Time: 2:10:15 PM
 */
public class Desktop
    implements Feature
{
    //-------------------------------------------------------------------------
    @Override
    public boolean expectedIn(Sighting inSighting)
    {
        return (inSighting.feature() instanceof Screen);
    }


    //-------------------------------------------------------------------------
    @Override
    public Iterable<? extends Sighting> locate(
            Sighting in)
    {
        TaskBarSighting sighting = TaskBarBorders.locate( in );

        return Arrays.asList(
                new SimpleSighting(this,
                    ( sighting == null )
                    ? in.area()
                    : sighting.orientation().inwardsExclusive(
                            in.area(), sighting.area())));
    }
    

    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "Desktop";
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
