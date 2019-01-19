package ao.dd.desktop.vision.feature.taskbar.border;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.vision.def.Feature;
import ao.dd.desktop.vision.impl.SimpleSighting;

/**
 * User: 188952
 * Date: Apr 18, 2010
 * Time: 4:29:08 PM
 */
public class OrientedSighting
        extends SimpleSighting
{
    //-------------------------------------------------------------------------
    private final Orientation orientation;


    //-------------------------------------------------------------------------
    public OrientedSighting(
            Feature     feature,
            Area        area,
            Orientation orientation)
    {
        super( feature, area );

        this.orientation = orientation;
    }


    //-------------------------------------------------------------------------
    public Orientation orientation()
    {
        return orientation;
    }


    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return super.feature() + " -> " + super.area()  +
                " [" + orientation + "]";
    }


    //-------------------------------------------------------------------------
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof OrientedSighting)) return false;
        if (! super.equals(o)) return false;

        OrientedSighting that = (OrientedSighting) o;
        return orientation == that.orientation;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result +
                (orientation != null ? orientation.hashCode() : 0);
        return result;
    }
}