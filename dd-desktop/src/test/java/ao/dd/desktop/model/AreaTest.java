package ao.dd.desktop.model;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.model.area.Areas;
import ao.dd.desktop.model.pixel.Pixels;
import org.testng.annotations.Test;

import java.awt.*;

import static org.testng.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: 188952
 * Date: Feb 27, 2010
 * Time: 4:09:13 AM
 */
public class AreaTest
{
    //-------------------------------------------------------------------------
    @Test
    public void testEmptyArea()
    {
        assertEquals(
                Areas.empty().topLeft(),
                Pixels.nullPixel(),
                "TopLeft of Empty Area");

        assertEquals(
                Areas.empty().bottomRight(),
                Pixels.nullPixel(),
                "BottomRight of Empty Area");

        assertEquals(
                Areas.empty().left(),
                Areas.empty(),
                "left of EmptyArea");

        assertEquals(
                Areas.empty().right(),
                Areas.empty(),
                "right of EmptyArea");

        assertEquals(
                Areas.empty().above(),
                Areas.empty(),
                "above EmptyArea");

        assertEquals(
                Areas.empty().below(),
                Areas.empty(),
                "below EmptyArea");

//        assertEquals("below EmptyArea",
//                Areas.empty(),
//                Areas.empty().locate("screenshots/documents.png"));

//        assertEquals(
//                Areas.empty().offset( Pixels.surroundingArea(10,10) ),
//                Areas.empty(),
//                "below EmptyArea");

        assertNull(Areas.empty().toRectangle(),
                   "convert EmptyArea to rectangle");

//        assertEquals(
//                Areas.empty().intersect(
//                        Areas.fromRectangles(new Rectangle(
//                                10,10,50,50))),
//                Areas.empty(),
//                "intersect EmptyArea with another Area");

//        assertEquals("intersect EmptyArea with another Area",
//                Iterators.emptyIterator(),
//                Areas.empty().iterator());

        assertEquals(
                "EmptyArea",
                Areas.empty().toString(),
                "EmptyArea to string");
    }
    
    //-------------------------------------------------------------------------
    @Test
    public void testAreaOffset()
    {
//        Area original  = Areas.fromRectangles(new Rectangle(10, 10, 400, 400));
//        Pixel offsetBy = Pixels.surroundingArea(50, 50);
//
//        Area newLocation = original.offset(offsetBy);
//
//        assertEquals(
//                newLocation,
//                Areas.fromRectangles(new Rectangle(60, 60, 400, 400)),
//                "checking translation of Area");
    }


    //-------------------------------------------------------------------------
    @Test
    public void relationalTest()
    {
//        Area boundary = Areas.fromRectangles(new Rectangle(49, 50, 1, 500));
//        Area rightOf  = boundary.right();
//
//        assertEquals(
//                rightOf,
//                Areas.fromRectangles(
//                        new Rectangle(50, 50, MAX_VALUE, 500)),
//                "checking rightOf Area");
//
//        Area leftOf  = boundary.left();
//        assertEquals(
//                leftOf,
//                Areas.fromRectangles(
//                        new Rectangle(0, 50, 49, 500)),
//                "checking Area.left");
//
//        boundary = Areas.fromRectangles(new Rectangle(0, 50, 500, 1));
//        Area above = boundary.above();
//        assertEquals(
//                above,
//                Areas.fromRectangles(new Rectangle(0, 0, 500, 50)),
//                "checking Area.above");
//
////        boundary = Areas.fromRectangles(new Rectangle(0, 50, 500, 1));
//        Area below = boundary.below();
//        assertEquals(
//                below,
//                Areas.fromRectangles(
//                        new Rectangle(0, 51, 500, MAX_VALUE)),
//                "checking Area.below");
    }


    //-------------------------------------------------------------------------
    @Test
    public void singleAreaTest()
    {
        Area boundary = Areas.newInstance(
                null, new Rectangle(49, 50, 1, 500));

        assertEquals(
                boundary.toRectangle(),
                new Rectangle(49, 50, 1, 500),
                "checking casting Area toRectangle.");

//        Area toSearchIn = Areas.fromRectangles(
//                new Screen().size());
//
//        assertEquals(
//                "locate a single Area",
//
//                79,41
    }


    //-------------------------------------------------------------------------
    @Test
    public void selectAreaTesting()
    {
    }

    //-------------------------------------------------------------------------
    @Test
    public void toStringTest()
    {
        Area toPrint = Areas.newInstance(
                null, new Rectangle(2, 8, 400, 50));

        String str = toPrint.toString();
        assertTrue(
                str.contains( "2"   ) &&
                str.contains( "8"   ) &&
                str.contains( "400" ) &&
                str.contains( "50"  ),
                "contains x, y, width, height.");
    }
}