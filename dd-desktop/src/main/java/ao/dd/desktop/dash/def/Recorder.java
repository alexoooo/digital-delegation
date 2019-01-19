package ao.dd.desktop.dash.def;

import ao.dd.desktop.model.area.Area;
import javax.swing.JComponent;

/**
 * User: alex
 * Date: 18-May-2010
 * Time: 10:51:21 PM
 */
public interface Recorder
{
    //-------------------------------------------------------------------------
    public JComponent view();

    public JComponent controls();
    

    //-------------------------------------------------------------------------
    public Area selection();
}
