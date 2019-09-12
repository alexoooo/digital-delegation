package ao.dd.desktop.control.mouse.destination;

import ao.dd.desktop.model.pixel.Pixel;

/**
 * User: 188952
 * Date: Apr 18, 2010
 * Time: 1:26:56 AM
 */
public class PixelMouseTarget
        extends AbstractMouseTarget
{
    //-------------------------------------------------------------------------
    private final Pixel destination;


    //-------------------------------------------------------------------------
    public PixelMouseTarget(Pixel target)
    {
        destination = target;
    }
    

    //-------------------------------------------------------------------------
    @Override
    public void move()
    {
        destination.display().point(
                destination.x(),
                destination.y());

        // this would cause recursion:
        //   destination.move();
    }
}
