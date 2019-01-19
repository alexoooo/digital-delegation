package ao.dd.desktop.vision.feature.taskbar;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.vision.def.Feature;
import ao.dd.desktop.vision.feature.taskbar.border.Orientation;
import ao.dd.desktop.vision.feature.taskbar.border.OrientedSighting;

/**
 * User: Mable
 * Date: Apr 10, 2010
 * Time: 5:30:20 PM
 */
public class TaskBarSighting
        extends OrientedSighting
{
    //-------------------------------------------------------------------------
    private final boolean isLocked;


    //-------------------------------------------------------------------------
    public TaskBarSighting(
            Feature     feature,
            Area        area,
            Orientation orientation,
            boolean     isLocked)
    {
        super( feature, area, orientation );

        this.isLocked = isLocked;
    }


    //-------------------------------------------------------------------------
    public boolean isLocked()
    {
        return isLocked;
    }


    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return super.toString()  +
                " [" +  (isLocked ? "locked" : "unlocked") + "]";
    }


    //-------------------------------------------------------------------------
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof TaskBarSighting)) return false;
        if (!super.equals(o)) return false;

        TaskBarSighting that = (TaskBarSighting) o;

        return isLocked == that.isLocked;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (isLocked ? 1 : 0);
        return result;
    }
}
