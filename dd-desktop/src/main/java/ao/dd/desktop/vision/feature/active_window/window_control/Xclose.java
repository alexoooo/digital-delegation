package ao.dd.desktop.vision.feature.active_window.window_control;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.model.display.Displays;
import ao.dd.desktop.model.image.ArrayRgbGrid;
import ao.dd.desktop.model.image.RgbGrid;
import ao.dd.desktop.util.Pictures;
import ao.dd.desktop.vision.def.Feature;
import ao.dd.desktop.vision.def.Sighting;
import ao.dd.desktop.vision.feature.Screen;
import ao.dd.desktop.vision.impl.SimpleSighting;
import ao.dd.desktop.vision.locate.SimpleLocator;
import com.google.common.collect.Iterables;
import java.util.Arrays;

/**
 * User: Eugene
 * Date: May 12, 2010
 * Time: 9:47:33 PM
 */
public class Xclose
    implements Feature
{
    //-------------------------------------------------------------------------
    @Override
    public boolean expectedIn(Sighting inSighting) 
    {
        return ( inSighting.feature() instanceof Screen );
    }
    
    @Override
    public Iterable<? extends Sighting> locate(Sighting inSighting) 
    {
        RgbGrid button = new ArrayRgbGrid(
                Pictures.toBufferedImage("xClose.png", Xclose.class),
                "xClose");

        Area spotted = Iterables.getOnlyElement(
                new SimpleLocator().locate(
                        Displays.mainScreen().asArea(), button
                ),
                null);

        return Arrays.asList(
                new SimpleSighting(
                        this,
                        spotted));
    }
    
    
    
    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "xClose";
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
