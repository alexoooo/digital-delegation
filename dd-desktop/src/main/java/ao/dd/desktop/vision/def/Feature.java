package ao.dd.desktop.vision.def;

/**
 * User: YM
 * Date: Apr 1, 2010
 * Time: 8:21:25 PM
 */
public interface Feature
{
//    //-------------------------------------------------------------------------
//    public String name();
//
//
//    //-------------------------------------------------------------------------
//    public Feature parent();


    //-------------------------------------------------------------------------
    public boolean expectedIn(Sighting featureSighting);
    

    //-------------------------------------------------------------------------
    public Iterable<? extends Sighting> locate(Sighting in);
}