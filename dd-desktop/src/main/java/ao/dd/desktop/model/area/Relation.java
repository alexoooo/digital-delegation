package ao.dd.desktop.model.area;

/**
 * User: 188952
 * Date: Apr 18, 2010
 * Time: 5:29:33 PM
 */
public enum Relation
{
    //-------------------------------------------------------------------------
    LEFT{
        @Override public Area apply(Area to) {
            return to.left();
        }},

    ABOVE{@Override public Area apply(Area to) {
            return to.above();
        }},

    BELOW{@Override public Area apply(Area to) {
            return to.below();
        }},

    RIGHT{@Override public Area apply(Area to) {
            return to.right();
        }};


    //-------------------------------------------------------------------------
    public abstract Area apply(Area to);


    //-------------------------------------------------------------------------
    public Relation invert()
    {
        switch (this)
        {
            case LEFT:  return RIGHT;
            case RIGHT: return LEFT;
            case ABOVE: return BELOW;
            case BELOW: return ABOVE;
            default:    return null;
        }
    }
}
