package ao.dd.desktop.vision.def;

import ao.dd.desktop.model.area.Area;

/**
 * User: 188952
 * Date: Apr 2, 2010
 * Time: 8:45:03 PM
 */
public interface Vision
{
    //-------------------------------------------------------------------------
    public Scene see(Area in);


    //-------------------------------------------------------------------------
    public void add(
            Feature feature);

    public void addAll(
            Iterable<? extends Feature> features);

    public void addAll(
            Feature... features);
}
