package ao.dd.desktop.vision.def;

/**
 * User: 188952
 * Date: Apr 2, 2010
 * Time: 8:41:55 PM
 */
public interface Scene
{
    //-------------------------------------------------------------------------
    public Sighting sighting();
//    public Area     area();


    //-------------------------------------------------------------------------
    public Iterable<Scene> kids();
}
