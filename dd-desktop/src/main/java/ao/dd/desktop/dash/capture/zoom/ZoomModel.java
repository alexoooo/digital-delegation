package ao.dd.desktop.dash.capture.zoom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 * Date: Aug 1, 2010
 * Time: 2:28:05 PM
 *
 * This model will ensure that upon zooming into some displayFocus,
 *   it's corresponding backingImageFocus will be the same before
 *   and after the zoom operation.  A special case of this is that if the
 *   zoom level stays constant, the image will pan about the displayFocus.
 *
 */
public class ZoomModel
{
    //------------------------------------------------------------------------
    private static final Logger LOG = LoggerFactory.getLogger(
            ZoomModel.class);


    //------------------------------------------------------------------------
    private int       zoomLevel;

    private Dimension backingSize;
    private Dimension displaySize;

    private Rectangle prevBackingRegion;
    private Rectangle prevDisplayRegion;

    private Point     backingFocus;
    private Point     backingFocusStart;


    //------------------------------------------------------------------------
    public ZoomModel(Dimension backingSize)
    {
        this(backingSize, new Dimension(0, 0));
    }

    public ZoomModel(
            Dimension backingSize,
            Dimension displaySize)
    {
        this.backingSize = backingSize;
        this.displaySize = displaySize;

        initZoom();
    }


    //------------------------------------------------------------------------
    private void initZoom()
    {
        zoomLevel = 0;

        Dimension regionSize = new Dimension(
                Math.min(backingSize.width , displaySize.width),
                Math.min(backingSize.height, displaySize.height));

        Point backingRegionDelta = new Point(
                divide(backingSize.width  - regionSize.width , 2),
                divide(backingSize.height - regionSize.height, 2));

        Point displayRegionDelta = new Point(
                divide(displaySize.width  - regionSize.width , 2),
                divide(displaySize.height - regionSize.height, 2));

        prevBackingRegion = new Rectangle(
                backingRegionDelta, regionSize);

        prevDisplayRegion = new Rectangle(
                displayRegionDelta, regionSize);
    }


    //------------------------------------------------------------------------
    public ZoomTransform setBackingSize(
            Dimension backingSize)
    {
        if (this.backingSize.equals( backingSize ))
        {
            return zoomTransform();
        }

        this.backingSize = backingSize;
        initZoom();

        return zoomTransform();
    }


    //------------------------------------------------------------------------
    public ZoomTransform setDisplaySize(
            Dimension newDisplaySize)
    {
        if (displaySize == null ||
            displaySize.width == 0 ||
            displaySize.height == 0)
        {
            displaySize = newDisplaySize;
            initZoom();
            return zoomTransform();
        }

        displaySize = newDisplaySize;
        capZoomLevel();

        Dimension nextBackingRegionSize =
                backingRegionSize( zoomLevel );

//        double widthRatio  = (double)
//                newDisplaySize.width  / displaySize.width;
//        double heightRatio = (double)
//                newDisplaySize.height / displaySize.height;
//
//        int nextBackingRegionWidth  =
//                Math.max(0,
//                Math.min(backingSize.width, (int)
//                Math.round(widthRatio  * prevBackingRegion.width)));
//        int nextBackingRegionHeight = Math.min(backingSize.height, (int)
//                Math.round(heightRatio * prevBackingRegion.height));
//        Dimension nextBackingRegionSize = new Dimension(
//                nextBackingRegionWidth, nextBackingRegionHeight);

        int backingRegionWidthDelta  =
                nextBackingRegionSize.width  - prevBackingRegion.width;
        int backingRegionHeightDelta =
                nextBackingRegionSize.height - prevBackingRegion.height;
        int nextBackingX = Math.max(0, Math.min(
                backingSize.width  - nextBackingRegionSize.width ,
                prevBackingRegion.x - (backingRegionWidthDelta  / 2)));
        int nextBackingY = Math.max(0, Math.min(
                backingSize.height  - nextBackingRegionSize.height,
                prevBackingRegion.y - (backingRegionHeightDelta / 2)));
        Point nextBackingRegionOffset = new Point(
                nextBackingX, nextBackingY);

        Rectangle nextBackingRegion = new Rectangle(
                nextBackingRegionOffset, nextBackingRegionSize);
        Rectangle nextDisplayRegion =
                displayRegion(nextBackingRegionSize, zoomLevel);

        ZoomTransform zoom = new ZoomTransform(
                nextBackingRegion, nextDisplayRegion);

        displaySize       = newDisplaySize;
        prevBackingRegion = nextBackingRegion;
        prevDisplayRegion = nextDisplayRegion;

        return zoom;
    }


    //------------------------------------------------------------------------
    public void moveFocus(
            Point displayFocus)
    {
        if (displayFocus == null)
        {
            backingFocus      = null;
            backingFocusStart = null;
        }
        else
        {
            backingFocus = backingPoint(
                    prevBackingRegion, prevDisplayRegion, displayFocus);
        }

        LOG.debug("Focus moved to: {}", backingFocus);
    }

    public void resetDrag()
    {
        backingFocusStart = backingFocus;
    }

    public Rectangle backingSelection(
            Dimension size, boolean firstAfterClipSizeFixation)
    {
        if (backingFocus == null) {
            LOG.info("no backingFocus in backingSelection: {}", size);
            return null;
        }

        LOG.debug("backingFocus: {}, size: {}", backingFocus, size);

        Point selectionOffset;
        if (firstAfterClipSizeFixation)
        {
            selectionOffset = new Point(
                    Math.max(0, backingFocus.x - size.width ),
                    Math.max(0, backingFocus.y - size.height));
        }
        else
        {
            selectionOffset = new Point(
                Math.max(0, Math.min(backingSize.width - size.width,
                        backingFocus.x - size.width  / 2)),
                Math.max(0, Math.min(backingSize.height - size.height,
                        backingFocus.y - size.height / 2)));
        }

        return new Rectangle( selectionOffset, size );
    }

    public Rectangle backingDrag()
    {
        if (backingFocusStart == null ||
            backingFocus == null)
        {
            return null;
        }

        Point topLeft    = new Point(
                Math.min(backingFocusStart.x, backingFocus.x),
                Math.min(backingFocusStart.y, backingFocus.y));

        Point bottomRight = new Point(
                Math.max(backingFocusStart.x, backingFocus.x),
                Math.max(backingFocusStart.y, backingFocus.y));

        Rectangle backingDrag = new Rectangle(topLeft,
                new Dimension(bottomRight.x - topLeft.x,
                              bottomRight.y - topLeft.y));
        return (backingDrag.width == 0 || backingDrag.height == 0)
               ? null : backingDrag;
    }


    //------------------------------------------------------------------------
//    public void pan(Direction direction)
//    {
//
//    }
//
//    private static enum Direction {
//        UP, DOWN, LEFT, RIGHT
//    }


    //------------------------------------------------------------------------
    public ZoomTransform changeZoomLevel(
            boolean zoomIn,
            Point   displayFocus)
    {
        int prevZoomLevel = zoomLevel;
        zoomLevel += (zoomIn ? 1 : -1);
        capZoomLevel();

        if (prevZoomLevel == zoomLevel) {
            return zoomTransform();
        }

        Point prevBackingFocus = backingPoint(
                prevBackingRegion, prevDisplayRegion, displayFocus);

        Dimension nextBackingRegionSize =
                backingRegionSize( zoomLevel );

        Rectangle nextDisplayRegion =
                displayRegion(nextBackingRegionSize, zoomLevel);
//        Rectangle nextDisplayRegion =
//                new Rectangle(new Point(0, 0), displaySize);
//        System.out.println("nextDisplayRegion: " + nextDisplayRegion);

        Point nextBackingFocusOffset =
                backingPoint(
                        new Rectangle(
                                new Point(0, 0),
                                nextBackingRegionSize),
                        nextDisplayRegion,
                        displayFocus);

        int nextBackingFocusDeltaX = Math.max(0, Math.min(
                backingSize.width  - nextBackingRegionSize.width ,
                prevBackingFocus.x - nextBackingFocusOffset.x));
        int nextBackingFocusDeltaY = Math.max(0, Math.min(
                backingSize.height  - nextBackingRegionSize.height,
                prevBackingFocus.y - nextBackingFocusOffset.y));
        Point nextBackingFocusDelta = new Point(
                nextBackingFocusDeltaX, nextBackingFocusDeltaY);

        Rectangle nextBackingRegion = new Rectangle(
                nextBackingFocusDelta, nextBackingRegionSize);

//        Point nextBackingFocus = backingPoint(
//                nextBackingRegion, nextDisplayRegion, displayFocus);
//        assert prevBackingFocus.equals( nextBackingFocus );

        ZoomTransform zoom = new ZoomTransform(
                nextBackingRegion, nextDisplayRegion);
//        if (! zoom.validate(backingSize)) {
//            System.out.println("wtf?");
//        }

        prevBackingRegion = nextBackingRegion;
        prevDisplayRegion = nextDisplayRegion;

        return zoom;
    }

    private void capZoomLevel()
    {
        for (int scale = zoomLevel; scale < 0; scale++)
        {
            Dimension scaled = scaleBacking(scale);
//            if (displaySize.width  <= scaled.width ||
//                displaySize.height <= scaled.height)
            if (displaySize.width  < scaled.width ||
                displaySize.height < scaled.height)
            {
                break;
            }
            zoomLevel++;
        }

        for (int scale = zoomLevel; scale > 0; scale--)
        {
            Dimension scaled = scaleBacking(scale);
            if (scaled.width  / displaySize.width  < 32 ||
                scaled.height / displaySize.height < 32)
            {
                break;
            }
            zoomLevel--;
        }
    }

    private Dimension backingRegionSize(int zoom)
    {
        Dimension scaledBacking = scaleBacking( zoom );

        double widthRatio  = (double)
                displaySize.width  / scaledBacking.width;
        double heightRatio = (double)
                displaySize.height / scaledBacking.height;

        int scaledWidth  = (int) Math.round(
                widthRatio * backingSize.width );
        int scaledHeight = (int) Math.round(
                heightRatio * backingSize.height);

        return new Dimension(
                Math.min(scaledWidth, backingSize.width ),
                Math.min(scaledHeight, backingSize.height));
    }


    //------------------------------------------------------------------------
    private Point backingPoint(
            Rectangle backingRegion,
            Rectangle displayRegion,
            Point     displayPoint)
    {
        if (displayPoint == null) {
            displayPoint = new Point(0, 0);
        }

        int clippedViewX = Math.min(displayRegion.width, Math.max(0,
                                displayPoint.x - displayRegion.x));
        int clippedViewY = Math.min(displayRegion.height, Math.max(0,
                                displayPoint.y - displayRegion.y));

        double viewPercentX = (double)
                clippedViewX / displayRegion.width;
        double viewPercentY = (double)
                clippedViewY / displayRegion.height;

        return new Point(
                (int)(backingRegion.x + viewPercentX * backingRegion.width),
                (int)(backingRegion.y + viewPercentY * backingRegion.height));
    }

    private Rectangle displayRegion(
            Dimension backingRegionSize, int zoom)
    {
        Dimension displayRegionSize =
                displayRegionSize(backingRegionSize, zoom);

        Point displayRegionOffset = new Point(
                (displaySize.width  - displayRegionSize.width ) / 2,
                (displaySize.height - displayRegionSize.height) / 2);

        return new Rectangle(
                displayRegionOffset, displayRegionSize);
    }

    private Dimension displayRegionSize(
            Dimension backingRegionSize, int zoom)
    {
        Dimension preferredDisplayRegionSize =
                preferredDisplayRegionSize(backingRegionSize, zoom);

        return new Dimension(
                Math.min(preferredDisplayRegionSize.width,
                            displaySize.width),
                Math.min(preferredDisplayRegionSize.height,
                            displaySize.height));
    }

    private Dimension preferredDisplayRegionSize(
            Dimension backingRegionSize, int zoom)
    {
        double zoomFactor = zoomFactor( zoom );
        return new Dimension(
                (int) Math.round(zoomFactor * backingRegionSize.width ),
                (int) Math.round(zoomFactor * backingRegionSize.height));
    }


    //------------------------------------------------------------------------
    public ZoomTransform zoomTransform()
    {
        return new ZoomTransform(
                prevBackingRegion, prevDisplayRegion);
    }


    //------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return zoomLevel + ", " +
               backingSize + ", " +
               displaySize + ", " +
               prevBackingRegion + ", " +
               prevDisplayRegion;
    }


    //------------------------------------------------------------------------
    private Dimension scaleBacking(int forZoomLevel)
    {
        double factor = zoomFactor(forZoomLevel);

        int inflatedWidth = (int) Math.round(
                backingSize.width  * factor);
        int inflatedHeight = (int) Math.round(
                backingSize.height * factor);

        return new Dimension(
                inflatedWidth, inflatedHeight);
    }

    private double zoomFactor(int forZoomLevel)
    {
        return (forZoomLevel < 0)
               ? Math.pow(0.9, -forZoomLevel) // zoom out
               : forZoomLevel + 1;            // zoom in
    }


    //------------------------------------------------------------------------
    private static int divide(int length, int factor)
    {
        return (int) Math.round((double) length / factor);
    }
}
