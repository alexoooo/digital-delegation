package ao.dd.desktop.model.area;

/**
 * Created by IntelliJ IDEA.
 * User: 188952
 * Date: Feb 27, 2010
 * Time: 1:36:05 AM
 */
public abstract class AbstractArea
        implements Area
{
//    //-------------------------------------------------------------------------
//    @Override
//    public Area offset(Pixel delta)
//    {
//        if (delta == Pixels.nullPixel()) return this;
//
//        Collection<Area> translated = Lists.newArrayList();
//
////        for (Area subArea : this)
////        {
////            translated.add(
////                    Pixels.surroundingArea(
////                            subArea.topLeft()    .offset( delta ),
////                            subArea.bottomRight().offset( delta)));
////        }
//
//        translated.add(
//                Pixels.surroundingArea(
//                        this.topLeft()    .offset( delta.x(), delta.y() )  ,
//                        this.bottomRight().offset( delta.x(), delta.y() )));
//
//        return Areas.fromAreas(translated);
//    }

    
//    //-------------------------------------------------------------------------
//    @Override
//    public Pixel topLeft() {
////        return select( Selectors.topLeft()     );
//        return Pixels.surroundingArea(
//                 display(), x(), y());
//    }
//
//    @Override
//    public Pixel topRight() {
////        return select( Selectors.topRight()    );
//        return Pixels.surroundingArea(
//                 display(),
//                 x() + width() - 1,
//                 y());
//    }
//
//    @Override
//    public Pixel bottomLeft() {
////        return select( Selectors.bottomLeft()  );
//        return Pixels.surroundingArea(
//                 display(),
//                 x(),
//                 y() + height() - 1);
//    }
//
//    @Override
//    public Pixel bottomRight() {
////        return select( Selectors.bottomRight() );
//        return Pixels.surroundingArea(
//                 display(),
//                 x() + width () - 1,
//                 y() + height() - 1);
//    }


//    private Pixel select ( Selector toSelect )
//    {
//        return toSelect.select();
//    }
}
