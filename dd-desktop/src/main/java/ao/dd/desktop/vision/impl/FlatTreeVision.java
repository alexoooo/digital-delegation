package ao.dd.desktop.vision.impl;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.vision.def.Feature;
import ao.dd.desktop.vision.def.Scene;
import ao.dd.desktop.vision.def.Sighting;
import ao.dd.desktop.vision.def.Vision;
import ao.dd.desktop.vision.feature.Screen;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * User: 188952
 * Date: Apr 2, 2010
 * Time: 8:46:59 PM
 */
public class FlatTreeVision
        implements Vision
{
    //-------------------------------------------------------------------------
    private final Collection<Feature> features;


    //-------------------------------------------------------------------------
    public FlatTreeVision()
    {
        features = new ArrayList<Feature>();
    }


    //-------------------------------------------------------------------------
    @Override
    public void addAll(Feature... features)
    {
        addAll(Arrays.asList( features ));
    }

    @Override
    public void addAll(Iterable<? extends Feature> features)
    {
        for (Feature feature : features)
        {
            add( feature );
        }
    }

    @Override
    public void add(Feature feature)
    {
        features.add( feature );
    }


    //-------------------------------------------------------------------------
    @Override
    public Scene see(Area in)
    {
        return see(Screen.sightingFrom( in ));
    }

    private Scene see(
            Sighting inSighting)
    {
        List<Scene> potentialKids =
                new ArrayList<Scene>();

        for (Feature feature : features)
        {
            if (feature.expectedIn( inSighting ))
            {
                for (Sighting sighting :
//                        feature.locate( inSighting.area() ))
                        FeatureCache.locate(feature, inSighting))
                {
                    if (sighting == null ||
                        sighting.equals( inSighting ))
                    {
                        continue;
                    }

                    potentialKids.add(
                            new SceneImpl(sighting));
                }
            }
        }

        SceneImpl from = new SceneImpl( inSighting );
        for (Scene kid : filterTopLevel( potentialKids ))
        {
            from.add(see(
                    kid.sighting()));
        }
        return from;
    }


    //-------------------------------------------------------------------------
    private Iterable<Scene> filterTopLevel(
            List<Scene> scenes)
    {
        List<Scene> topLevel = new ArrayList<Scene>();

        next_ref_scene:
        for (int i = 0; i < scenes.size(); i++)
        {
            Scene ref    = scenes.get( i );
            Area  relLoc = ref.sighting().area();

            for (int j = i + 1; j < scenes.size(); j++)
            {
                Scene vs    = scenes.get( j );
                Area  vsLoc = vs.sighting().area();

                Rectangle intersection =
                        relLoc.intersect(
                            vsLoc
                        ).toRectangle();
                
                if (intersection != null    &&
                    intersection.width  > 0 &&
                    intersection.height > 0)
                {
                    if (! isLargerThan(
                            relLoc.toRectangle(),
                            vsLoc .toRectangle()))
                    {
                        continue next_ref_scene;
                    }
                }
            }

            topLevel.add( ref );
        }

        return topLevel;
    }


    //-------------------------------------------------------------------------
    private boolean isLargerThan(
            Rectangle a, Rectangle b)
    {
        return (a.width * a.height) >
               (b.width * b.height);
    }
}
