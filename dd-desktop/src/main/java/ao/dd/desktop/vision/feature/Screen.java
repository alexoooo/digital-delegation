package ao.dd.desktop.vision.feature;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.vision.def.Feature;
import ao.dd.desktop.vision.def.Sighting;
import ao.dd.desktop.vision.impl.SimpleSighting;

import java.util.Arrays;

/**
 * User: 188952
 * Date: Apr 2, 2010
 * Time: 8:54:51 PM
 */
public class Screen
        implements Feature
{
    //-------------------------------------------------------------------------
    public static Sighting sightingFrom(
            Area area)
    {
        return new SimpleSighting(
                    new Screen(), area);
    }


    //-------------------------------------------------------------------------
    @Override
    public boolean expectedIn(Sighting sighting)
    {
        return (sighting == null);
    }


    //-------------------------------------------------------------------------
    @Override
    public Iterable<SimpleSighting> locate(
            Sighting in)
    {
        return Arrays.asList(new SimpleSighting(
                this, in.area()));

//        return Arrays.asList(new SimpleSighting(
//                this, in));

//                Areas.fromRectangles(
//                        new Rectangle(
//                                0, 0,
//                                within.getWidth(),
//                                within.getHeight()
//                        ))));
    }


    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "Screen";
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
