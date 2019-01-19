package ao.dd.desktop.vision.impl;

import ao.dd.desktop.vision.def.Scene;
import ao.dd.desktop.vision.def.Sighting;
import ao.util.text.Txt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: 188952
 * Date: Apr 2, 2010
 * Time: 8:49:02 PM
 */
public class SceneImpl
        implements Scene
{
    //-------------------------------------------------------------------------
    private final Sighting sighting;
//    private final Area     area;

    private final List<Scene> kids;


    //-------------------------------------------------------------------------
    public SceneImpl(
//            Sighting depicting,
//            Area     in)
            Sighting topSighting)
    {
//        sighting = depicting;
//        area     = in;

        sighting = topSighting;
        kids     = new ArrayList<Scene>();
    }


    //-------------------------------------------------------------------------
    @Override
    public Sighting sighting()
    {
        return sighting;
    }

//    @Override
//    public Area area()
//    {
//        return sighting.area();
//    }


    //-------------------------------------------------------------------------
    public void add(Scene kid)
    {
        kids.add( kid );
    }


    //-------------------------------------------------------------------------
    @Override
    public Iterable<Scene> kids()
    {
        return kids;
    }


    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return toString(0);
    }

    private String toString(int depth)
    {
        StringBuilder str = new StringBuilder();

        str.append(Txt.nTimes("\t", depth))
           .append(sighting);

        int nextDepth = depth + 1;

        for (Scene kid : kids())
        {
            str.append("\n")
               .append(((SceneImpl) kid).toString( nextDepth ));
        }

        return str.toString();
    }
}
