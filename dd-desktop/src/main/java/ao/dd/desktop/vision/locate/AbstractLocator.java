package ao.dd.desktop.vision.locate;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.model.area.Areas;
import ao.dd.desktop.model.image.SegmentGrid;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: alex
 * Date: 24-May-2010
 * Time: 10:42:03 PM
 */
public abstract class AbstractLocator
        implements Locator
{
    //-------------------------------------------------------------------------
    @Override
    public List<Area> locate(Area inArea, SegmentGrid target)
    {
        List<Area> locations = new ArrayList<Area>();
        for (Rectangle location : locate(
                inArea.capture(), target))
        {
            locations.add(Areas.newInstance(
                    inArea.display(), location));
        }
        return locations;
    }


    //-------------------------------------------------------------------------
    @Override
    public abstract List<Rectangle>
            locate(SegmentGrid in, SegmentGrid target);


}
