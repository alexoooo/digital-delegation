package ao.dd.desktop.vision.feature;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.model.image.SegmentGrid;
import ao.dd.desktop.vision.def.Feature;
import ao.dd.desktop.vision.def.Sighting;
import ao.dd.desktop.vision.impl.SimpleSighting;
import ao.dd.desktop.vision.locate.Locator;
import ao.dd.desktop.vision.locate.SimpleLocator;
import com.google.common.collect.Lists;
import java.util.List;

/**
 * User: alex
 * Date: 13-Apr-2010
 * Time: 11:49:40 PM
 */
public class ImageFeature
        implements Feature
{
    //-------------------------------------------------------------------------
    private static final Locator locator =
            new SimpleLocator();


    //-------------------------------------------------------------------------
    private final SegmentGrid target;
    private final String      name;


    //-------------------------------------------------------------------------
    public ImageFeature(
            SegmentGrid targetImage)
    {
        this(targetImage, targetImage.toString());
    }

    public ImageFeature(
            SegmentGrid targetImage,
            String      targetName)
    {
        target = targetImage;
        name   = targetName;
    }


    //-------------------------------------------------------------------------
    @Override
    public boolean expectedIn(Sighting feature)
    {
        return ! feature.feature().equals( this );
    }

    
    //-------------------------------------------------------------------------
    @Override
    public Iterable<Sighting> locate(Sighting in)
    {
        List<Sighting> locations = Lists.newArrayList();

        for (Area area : locator.locate(in.area(), target))
        {
            locations.add(
                    new SimpleSighting(
                            this, area));
        }

        return locations;
    }


    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return name;
    }
    

    //-------------------------------------------------------------------------
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ImageFeature)) return false;

        ImageFeature that = (ImageFeature) o;

        return target.equals(that.target);
    }

    @Override
    public int hashCode()
    {
        return target.hashCode();
    }
}
