package ao.dd.desktop.dash.model.field;

import ao.dd.desktop.dash.capture.attrib.ClipSizeSelection;
import ao.dd.desktop.dash.capture.control.ScreenClipperFlow;
import ao.dd.desktop.dash.capture.impl.ZoomingClipper;
import ao.dd.desktop.model.image.RgbGrid;
import ao.util.persist.PersistentBytes;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Date: 3/26/11
 * Time: 10:05 PM
 */
public class BoundArchetypeField
{
    //------------------------------------------------------------------------
    private boolean allowResize;
//    private RgbGrid image;
    private BufferedImage image;


    //------------------------------------------------------------------------
    public BoundArchetypeField()
    {
        allowResize = true;
    }

    private BoundArchetypeField(
            boolean allowResize, BufferedImage image)
    {
        this.allowResize = allowResize;
        this.image       = image;
    }


    //------------------------------------------------------------------------
    public void setAllowResize(boolean allowResize)
    {
        this.allowResize = allowResize;
    }


    //------------------------------------------------------------------------
    public Dimension getDimension()
    {
        return (image == null ? null :
                new Dimension(image.getWidth ( null ),
                              image.getHeight( null )));
    }


    //------------------------------------------------------------------------
    private BufferedImage scale(BufferedImage src, int maxSide) {
        int w = src.getWidth( null );
        int h = src.getHeight( null );
        if (w <= maxSide && h <= maxSide) {
            return src;
        }

        int max = Math.max(w, h);
        double ratio = (double) maxSide / max;

        return scale(src, (int)(w * ratio), (int)(h * ratio));
    }

    private BufferedImage scale(BufferedImage src, int w, int h) {
        int type = BufferedImage.TYPE_INT_RGB;
        BufferedImage dst = new BufferedImage(w, h, type);
        Graphics2D g2 = dst.createGraphics();
        // Fill background for scale to fit.
        g2.setBackground(UIManager.getColor("Panel.background"));
        g2.clearRect(0,0,w,h);
        double xScale = (double)w/src.getWidth();
        double yScale = (double)h/src.getHeight();
        // Scaling options:
        // Scale to fit - image just fits in label.
        double scale = Math.min(xScale, yScale);
        // Scale to fill - image just fills label.
        //double scale = Math.max(xScale, yScale);
        int width  = (int)(scale*src.getWidth());
        int height = (int)(scale*src.getHeight());
        int x = (w - width)/2;
        int y = (h - height)/2;
        g2.drawImage(src, x, y, width, height, null);
        g2.dispose();
        return dst;
    }


    //------------------------------------------------------------------------
    public JComponent viewReadOnly()
    {
        if (image == null) {
            return new JLabel("N/A");
        } else {
            JPanel p = new JPanel(new MigLayout("nocache"));

//            JComponent i = new ImagePanel( scale(image, 250) );
//            p.add(new ImagePanel( image ), "wrap");
//            i.setSize(200, 200);
//            p.add(i, "wrap");

            JComponent c = new JLabel(new ImageIcon(
                    scale(image, 250) ));
//            c.setBackground(Color.BLACK);
            c.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
//            c.setSize(new Dimension(image.getWidth(), image.getHeight()));
//            c.setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));

//            JPanel b = new JPanel(new GridLayout(1, 1));
//            JScrollPane cScroll = new JScrollPane(c);
//            cScroll.setBackground(Color.BLACK);
//            cScroll.setBorder(BorderFactory.createLineBorder(
//                    Color.BLACK, 2));
//            cScroll.setMaximumSize(new Dimension(250, 250));
//            b.add( cScroll );
//            p.add(cScroll, "width 100%, height 100%, wrap");
//            p.add( b, "span, wrap");
            p.add( c, "wrap");

            Dimension dim = getDimension();
            p.add( new JLabel(dim.width + "w x " + dim.height + "h") );
            return p;
        }
    }

    public JComponent viewEditable(
            final Listener listener)
    {
        final ZoomingClipper clipper = new ZoomingClipper();

        clipper.allowSizeChange( allowResize );
        if (! allowResize) {
            clipper.setClipSize(
                    image.getWidth (null),
                    image.getHeight(null));
        }

        JPanel clipperPanel = new JPanel(new MigLayout());

//        JPanel i = new JPanel(new GridLayout(1, 0));
//        i.add( clipper );
//        JScrollPane o = new JScrollPane( i );
//        o.setPreferredSize(new Dimension(250, 250));
//        clipperPanel.add(o, "wrap");
        clipperPanel.add(clipper, "wrap");
        clipperPanel.add(new ScreenClipperFlow( clipper ));

        JButton captureFeatureButton = new JButton("Capture");
        captureFeatureButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                RgbGrid clip = clipper.clip();
                if (clip == null) {
                    return;
                }

                image = clip.toBufferedImage();
                listener.selectedOrCancelled();
            }});

        JButton backButton = new JButton("<< Back");
        backButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                listener.selectedOrCancelled();
            }});

        JPanel controlPanel = new JPanel(new MigLayout());
        controlPanel.add(backButton, "wrap");
        controlPanel.add(new ClipSizeSelection(clipper), "wrap");
        controlPanel.add(captureFeatureButton);

        JPanel p = new JPanel(new MigLayout());
        p.add( controlPanel );
        p.add( clipperPanel );
        return p;
    }


    //------------------------------------------------------------------------
    public void write(File dir)
    {
        try {
            ImageIO.write(image, "png", new File(dir, "archetype.png"));

            PersistentBytes.persist(
                    new byte[]{(byte)(allowResize ? 1 : 0)},
                    new File(dir, "allow-resize.bool"));
        } catch (IOException e) {
            throw new Error( e );
        }
    }

    public static BoundArchetypeField read(File dir)
    {
        try {
            BufferedImage image =
                    ImageIO.read(new File(dir, "archetype.png"));

            boolean allowResize = PersistentBytes.retrieve(
                    new File(dir, "allow-resize.bool"))[ 0 ] == 1;

            return new BoundArchetypeField(
                    allowResize, image);
        } catch (IOException e) {
            throw new Error( e );
        }
    }


    //------------------------------------------------------------------------
    public interface Listener
    {
        void selectedOrCancelled();
    }
}
