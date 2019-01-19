package ao.dd.desktop.dash;

import ao.dd.desktop.dash.capture.zoom.ZoomModel;
import ao.dd.desktop.dash.capture.zoom.ZoomTransform;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.*;

/**
 * User: Mable
 * Date: Aug 1, 2010
 * Time: 7:29:26 PM
 */
public class ZoomModelTest
{
    //------------------------------------------------------------------------
    @Test
    public void flatFullViewTest()
    {
        ZoomModel model = new ZoomModel(
                new Dimension(100, 100),
                new Dimension(100, 100));
    }


    //------------------------------------------------------------------------
    @Test
    public void flatHalfDefaultTest()
    {
        ZoomModel model = new ZoomModel(
                new Dimension(100, 100),
                new Dimension(50, 50));

        Assert.assertEquals(
                model.zoomTransform().backingArea(),
                new Rectangle(25, 25, 50, 50));
        Assert.assertEquals(
                model.zoomTransform().displayArea(),
                new Rectangle(0, 0, 50, 50));
    }


    //------------------------------------------------------------------------
    @Test
    public void flatZoomFocusTest()
    {
        ZoomModel model = new ZoomModel(
                new Dimension(100, 100),
                new Dimension(100, 100));

        ZoomTransform zoom = model.changeZoomLevel(
                true, new Point(50, 50));
        Assert.assertEquals(
                zoom.backingArea(),
                new Rectangle(25, 25, 50, 50));
    }


    //------------------------------------------------------------------------
//    @Test
//    public void flatResizeTest()
//    {
//        ZoomModel model = new ZoomModel(
//                new Dimension(100, 100),
//                new Dimension(50, 50));
//
//        ZoomTransform zoom = model.changeZoomLevel(
//                1, new Point(50, 50));
//        Assert.assertEquals(
//                zoom.backingArea(),
//                new Rectangle(25, 25, 50, 50));
//    }
}
