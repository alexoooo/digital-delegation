package ao.dd.desktop.dash.capture;

import ao.dd.desktop.model.image.RgbGrid;

import java.awt.*;

/**
 * User: AO
 * Date: Jul 18, 2010
 * Time: 5:45:35 PM
 */
public interface Clipper
{
    //------------------------------------------------------------------------
    /**
     * @param subject to use to pic selection() : Area
     */
    public void setSubject(
            RgbGrid subject);


    //------------------------------------------------------------------------
    /**
     * @return some agent-selected subsection of the subject
//     * @see ao.dd.desktop.dash.def.Clipper.setSource(Area subject)
     */
    public RgbGrid clip();

//    /**
//     * @return returns the next clip() : Area that is different from
//     *              the previous.
//     *         On the first call, returns the first available clip.
//     */
//    public RgbGrid nextClip();


    //------------------------------------------------------------------------
    public void setClipSize(
            int width, int height);

    public void allowSizeChange(boolean allow);
//    public void setAllowClipSizeDrag(boolean isClipSizeDragAllowed);

//    public boolean isClipSizeDragAllowed();


    //------------------------------------------------------------------------
    public void addListener(Listener listener);


    //------------------------------------------------------------------------
    public static interface Listener
    {
        public void selectionChanged(
                Rectangle selection);
    }
}
