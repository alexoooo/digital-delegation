package ao.dd.desktop.vision.impl;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.model.area.Areas;
import ao.dd.desktop.vision.def.Feature;
import ao.dd.desktop.vision.def.Sighting;

/**
 * User: Mable
 * Date: Apr 10, 2010
 * Time: 5:15:52 PM
 */
public class SimpleSighting
        implements Sighting
{
    //-------------------------------------------------------------------------
    private final Feature feature;
    private final Area    area;


    //-------------------------------------------------------------------------
    public SimpleSighting(
            Feature sightedFeature,
            Area    sightedArea)
    {
        feature = sightedFeature;
        area    = sightedArea;
    }


    //-------------------------------------------------------------------------
    @Override
    public Area area()
    {
        return (area == null)
                ? Areas.empty()
                : area;
    }

    @Override
    public Feature feature()
    {
        return feature;
    }


    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return feature + " -> " + area;
    }


    //-------------------------------------------------------------------------
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || !(o instanceof Sighting)) return false;

        Sighting that = (Sighting) o;

        return !(area != null
                 ? !area.equals(that.area())
                 : that.area() != null) &&
               !(feature != null
                 ? !feature.equals(that.feature())
                 : that.feature() != null);
    }

    @Override
    public int hashCode()
    {
        int result = area != null ? area.hashCode() : 0;
        result = 31 * result +
                (feature != null ? feature.hashCode() : 0);
        return result;
    }
}
