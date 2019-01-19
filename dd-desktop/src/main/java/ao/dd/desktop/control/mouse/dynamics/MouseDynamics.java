package ao.dd.desktop.control.mouse.dynamics;

import ao.dd.desktop.control.mouse.MouseButton;
import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.model.pixel.Pixel;

/**
 * User: 188952
 * Date: Apr 18, 2010
 * Time: 12:57:30 AM
 */
public interface MouseDynamics
{
    //-------------------------------------------------------------------------
    public Pixel[] planMotion(Pixel from, Pixel to);
    public Pixel[] planMotion(Pixel from, Area  to);
    
    public void move(Pixel... path);


    //-------------------------------------------------------------------------
    public long clickPause(MouseButton button);
    public long multiClickPause();
}
