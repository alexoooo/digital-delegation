package ao.dd.desktop.vision.feature.taskbar.border;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.model.area.Areas;
import ao.dd.desktop.model.area.Relation;
import ao.dd.desktop.model.pixel.Pixel;
import ao.dd.desktop.model.pixel.Pixels;
import ao.dd.desktop.util.DesktopUtils;

/**
 * User: 188952
 * Date: Apr 3, 2010
 * Time: 1:50:41 AM
 */
public enum Orientation
{
    //-------------------------------------------------------------------------
    //     Dx, Dy,  Sx, Sy,  OFx, OFy
    TOP   (1,  0,   0,  0,    0,   1, Relation.BELOW),
    BOTTOM(1,  0,   0, -1,    0,  -1, Relation.ABOVE),
    RIGHT (0,  1,  -1,  0,   -1,   0, Relation.LEFT),
    LEFT  (0,  1,   0,  0,    1,   0, Relation.RIGHT);

//    public static final Orientation VALUES[] = values();


    //-------------------------------------------------------------------------
    private final int startX;
    private final int startY;

    private final int lineDirectionX;   // left to right
    private final int lineDirectionY;   // top  to bottom

    private final int towardsScreenCenterX;
    private final int towardsScreenCenterY;

    private final Relation towardCenter;


    //-------------------------------------------------------------------------
    private Orientation(
            int lineDx,   int lineDy,
            int perDx,    int perDy,
            int centerDx, int centerDy,
            Relation      towardsCenter)
    {
        startX  = perDx;
        startY  = perDy;

        lineDirectionX = lineDx;
        lineDirectionY = lineDy;

        towardsScreenCenterX = centerDx;
        towardsScreenCenterY = centerDy;

        towardCenter = towardsCenter;
    }


    //-------------------------------------------------------------------------
    public Area line(
            Area in,
            int  skipTowardsScreenCenter)
    {
        return Areas.surroundingArea(
                start(in, skipTowardsScreenCenter),
                end  (in, skipTowardsScreenCenter));
    }

    public Area outwards(
            Area in,
            Area from)
    {
        return in.intersect(
                 towardCenter.invert().apply( from ));
    }


    //-------------------------------------------------------------------------
    public Area inwardsExclusive(
            Area in, Area fromBorder)
    {
        return in.intersect(
                 towardCenter.apply( fromBorder ));
    }

    public Area inwardsInclusive(
            Area in,
            Area fromLine,
            int  thickness)
    {
        if ( fromLine == null || fromLine.equals( Areas.empty() ))
        {
            return Areas.empty();
        }

        assert in.display().equals(
               fromLine.display() );

        return Areas.surroundingArea(
                 fromLine.topLeft(),
                 fromLine.bottomRight().offset(
                        towardsScreenCenterX * thickness,
                        towardsScreenCenterY * thickness)
               ).intersect( in );
    }
    

    //-------------------------------------------------------------------------
    public Pixel start(
            Area in,
            int  skipTowardsScreenCenter)
    {
        return Pixels.newInstance(
                in.display(),
                startX(in) + towardsScreenCenterX() * skipTowardsScreenCenter,
                startY(in) + towardsScreenCenterY() * skipTowardsScreenCenter);
    }

    public Pixel end(
            Area in,
            int  skipTowardsScreenCenter)
    {
        boolean isVertical = (lineDirectionY > 0);

        return start(in, skipTowardsScreenCenter).offset(
                   isVertical ? 0 : in.width()  - 1,
                 ! isVertical ? 0 : in.height() - 1);
    }


    //-------------------------------------------------------------------------
    public int startX(Area in)
    {
        return in.x() + DesktopUtils.abs(
                          startX, in.width());
    }

    public int startY(Area in)
    {
        return in.y() + DesktopUtils.abs(
                          startY, in.height());
    }


    //-------------------------------------------------------------------------
    public int lineDirectionX()
    {
        return lineDirectionX;
    }

    public int lineDirectionY()
    {
        return lineDirectionY;
    }


    //-------------------------------------------------------------------------
    public int towardsScreenCenterX()
    {
        return towardsScreenCenterX;
    }

    public int towardsScreenCenterY()
    {
        return towardsScreenCenterY;
    }


    //-------------------------------------------------------------------------
    public boolean isHorizontal()
    {
        return (this == Orientation.TOP ||
                this == Orientation.BOTTOM);
    }
}
