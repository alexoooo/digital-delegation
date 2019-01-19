package ao.dd.desktop.dash.capture.impl;

import ao.dd.desktop.dash.capture.Clipper;
import ao.dd.desktop.dash.capture.zoom.ZoomModel;
import ao.dd.desktop.dash.capture.zoom.ZoomTransform;
import ao.dd.desktop.model.display.Displays;
import ao.dd.desktop.model.image.RgbGrid;
import ao.util.async.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static java.awt.event.InputEvent.SHIFT_DOWN_MASK;
import static java.awt.event.KeyEvent.VK_EQUALS;

/**
 * User: Alexo
 * Date: Aug 1, 2010
 * Time: 5:42:48 PM
 */
public class ZoomingClipper
        extends    JComponent
        implements MouseListener,
                   MouseMotionListener,
                   MouseWheelListener,
                   ComponentListener,
                   Clipper
{
    //------------------------------------------------------------------------
    private static final Logger LOG = LoggerFactory.getLogger(
            ZoomingClipper.class);


    //------------------------------------------------------------------------
    private RgbGrid   backingImage;
    private ZoomModel model;

    private ZoomTransform transform = null;

    private Point displayFocus;

    private Dimension clipSize;
    private boolean   allowSizeChange   = true;
    private boolean   afterSizeFixation = false;

    private Publisher<Listener> publisher = new Publisher<Listener>();


    //------------------------------------------------------------------------
    public ZoomingClipper()
    {
        backingImage =
                Displays.mainScreen().asArea().capture();

        model = new ZoomModel(
                new Dimension(
                        backingImage.width(),
                        backingImage.height()));

        addMouseListener      (this);
        addMouseMotionListener(this);
        addMouseWheelListener (this);
        addComponentListener  (this);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(VK_EQUALS, SHIFT_DOWN_MASK), "zoom-in");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke('='), "zoom-in");
        getActionMap().put("zoom-in", new ZoomAction(true));
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke('-'), "zoom-out");
        getActionMap().put("zoom-out", new ZoomAction(false));

        setFocusable(true);
    }


    //------------------------------------------------------------------------
    @Override
    public void setSubject(RgbGrid subject)
    {
        backingImage = subject;
        transform    = model.setBackingSize(new Dimension(
                backingImage.width(), backingImage.height()));

        repaint();
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
//        return new Dimension(250, 250);
    }


    //------------------------------------------------------------------------
    @Override
    public void paint(Graphics g)
    {
        if (transform == null) {
            return;
        }

        Rectangle selection = clipRectangle();
        LOG.debug("selection: {}", selection);

        transform.apply(
                backingImage, selection, g);

        String zoomMessage = transform.toString();
        
        g.setColor(new Color(255, 0, 0, 200));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        g.drawString(zoomMessage, getWidth() - 100, getHeight());
    }


    //------------------------------------------------------------------------
    private class ZoomAction extends AbstractAction {
        private final boolean zoomIn;

        public ZoomAction(boolean zoomIn) {
            this.zoomIn = zoomIn;
        }

        @Override public void actionPerformed(ActionEvent e) {
            transform = model.changeZoomLevel(zoomIn, displayFocus);
            repaint();
        }
    }

//    private class PanAction extends AbstractAction {
//        private final boolean zoomIn;
//
//        public PanAction() {
//            this.zoomIn = zoomIn;
//        }
//
//        @Override public void actionPerformed(ActionEvent e) {
//            model.pan();
//
//            transform = model.changeZoomLevel(zoomIn, displayFocus);
//            repaint();
//        }
//    }


    //------------------------------------------------------------------------
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        updateFocus( e, false );

        new ZoomAction(e.getUnitsToScroll() < 0)
                .actionPerformed( null );
    }

    @Override public void mousePressed(MouseEvent e)
    {
        updateFocus( e, true );
        if (allowSizeChange) {
            model.resetDrag();
        }
        afterSizeFixation = false;
    }
    @Override public void mouseReleased(MouseEvent e)
    {
        updateFocus( e, true );
    }

    @Override public void mouseMoved(MouseEvent e) {
        requestFocusInWindow();
    }

    @Override public void mouseDragged(MouseEvent e)
    {
        updateFocus( e, true );
    }

    private void updateFocus(
            MouseEvent e, boolean moveFocus)
    {
        displayFocus = e.getPoint();

        if (moveFocus)
        {
            model.moveFocus( displayFocus );
        }

        fireChange();
        repaint();
    }

    @Override
    public void componentResized(ComponentEvent e)
    {
//        updateDisplaySize();
        transform = model.setDisplaySize( getSize() );
        repaint();
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void componentMoved(ComponentEvent e) {}
    @Override public void componentShown(ComponentEvent e) {}
    @Override public void componentHidden(ComponentEvent e) {}


    //-------------------------------------------------------------------------
    @Override
    public RgbGrid clip()
    {
        Rectangle clipRectangle = clipRectangle();
        LOG.info("clipRectangle: {}", clipRectangle);

        return clipRectangle == null
               ? null : backingImage.sub( clipRectangle );
    }

    private Rectangle clipRectangle()
    {
        if (allowSizeChange)
        {
            return model.backingDrag() != null
                    ? model.backingDrag()
                    : null;
        }
        else
        {
            return model.backingSelection(
                    clipSize, afterSizeFixation);
        }
    }

    @Override
    public void setClipSize(int width, int height)
    {
        if (clipSize == null ||
            clipSize.width  != width ||
            clipSize.height != height)
        {
            clipSize = new Dimension(width, height);
            fireChange();
        }
    }

    @Override
    public void allowSizeChange(boolean allow)
    {
        boolean changed = (allowSizeChange != allow);
        allowSizeChange = allow;
        if (allow)
        {
            clipSize = null;
            model.moveFocus( null );

            fireChange();
            repaint();
        }
        else if (changed)
        {
            afterSizeFixation = true;
        }
    }

    @Override
    public void addListener(Listener listener)
    {
        publisher.subscribe( listener );
    }
    private void fireChange()
    {
        publisher.publish(new Publisher.Distributor<
                Clipper.Listener>() {
            @Override public void deliverTo(Listener listener) {
                Rectangle selection = clipRectangle();
                listener.selectionChanged( selection );
            }});
    }
}
