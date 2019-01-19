package ao.dd.desktop.vision.feature.start_button;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.model.area.Areas;
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
import java.util.Collections;


/**
 * User: Eugene
 * Date: May 12, 2010
 * Time: 6:45:40 PM
 */
public class StartButton
    implements Feature
{
    //-------------------------------------------------------------------------
    @Override
    public boolean expectedIn(Sighting featureSighting)
    {
        return (featureSighting.feature() instanceof Screen);
    }


    //-------------------------------------------------------------------------
    @Override
    public Iterable<? extends Sighting> locate(
            Sighting in)
    {
        RgbGrid target = new ArrayRgbGrid(
                Pictures.toBufferedImage( "start.png", StartButton.class));

        Area startButton = Iterables.getOnlyElement(
                new SimpleLocator().locate(
                        Displays.mainScreen().asArea(), target
                ), null);

        return ( startButton == null ||
                    startButton.equals( Areas.empty()) )
                ? Collections.<Sighting>emptyList()
                : Arrays.asList(
                    new SimpleSighting( this, startButton));
    }


    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "Start Button";
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