package ao.dd.desktop.vision.impl;

import ao.dd.desktop.model.image.RgbGrid;
import ao.dd.desktop.vision.def.Feature;
import ao.dd.desktop.vision.def.Sighting;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * User: 188952
 * Date: Apr 19, 2010
 * Time: 1:47:43 AM
 */
public class FeatureCache
{
    //-------------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(
                    FeatureCache.class);

    private static final boolean ENABLE_CACHE = true;

    private static final Map<Long, Iterable<? extends Sighting>>
            locationCache = new LinkedHashMap<
                              Long, Iterable<? extends Sighting>>(){
                    @Override protected boolean
                            removeEldestEntry(Map.Entry e) {
                        return size() > 100;
                }};


    //-------------------------------------------------------------------------
    private FeatureCache() {}


    //-------------------------------------------------------------------------
    public synchronized static
            Iterable<? extends Sighting> locate(
                    Feature feature, Sighting in)
    {
        //,ConstantConditions
        if (! ENABLE_CACHE) {
            return feature.locate( in );                      
        }

        RgbGrid image     = in.area().capture();
        Long       imageCode =
                (long) feature.hashCode() ^ image.hashCode();

        Iterable<? extends Sighting> location =
                locationCache.get( imageCode );
        if (location == null)
        {
            location = feature.locate( in );
            locationCache.put(imageCode, location);

            LOG.trace("caching " + feature + " (" +
                        in + ", " + imageCode + ")");
        }
        else
        {
            LOG.trace("cached " + feature + " (" + in + ")");
        }
        return location;
    }


//    //-------------------------------------------------------------------------
//    private static Long hashCode(
//            BufferedImage image, Feature feature)
//    {
//        long hash = feature.hashCode() *
//                    image.getWidth() ^ image.getHeight();
//        for (int x = 0; x < image.getWidth(); x++)
//        {
//            for (int y = 0; y < image.getHeight(); y++)
//            {
//                hash += image.getRGB(x, y);
//            }
//        }
//        return hash;
//    }
}
