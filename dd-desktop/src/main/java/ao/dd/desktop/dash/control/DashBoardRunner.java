package ao.dd.desktop.dash.control;

import ao.dd.desktop.dash.capture.attrib.ClipSizeSelection;
import ao.dd.desktop.dash.capture.control.ScreenClipperFlow;
import ao.dd.desktop.dash.capture.impl.ZoomingClipper;
import ao.dd.desktop.model.display.Displays;
import ao.dd.desktop.model.image.RgbGrid;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Date: 18-May-2010
 * Time: 9:46:44 PM
 */
public class DashBoardRunner
{
    //-------------------------------------------------------------------------
    private static final Logger LOG =
            LoggerFactory.getLogger(DashBoardRunner.class);


    //-------------------------------------------------------------------------
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                setLookAndFeel("Nimbus");
                buildAndDisplayGui();
            }});
    }


    //-------------------------------------------------------------------------
    private static void buildAndDisplayGui()
    {
        JFrame frame = new JFrame("Interactive Object Recognition");
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frame.getContentPane().add(
//                buildMainGuiPanel());
                new DashBoardPanel());

//        frame.setMaximumSize(new Dimension(500, 500));
//        frame.pack();
        frame.setSize(
                Displays.mainScreen().width() / 2,
                Displays.mainScreen().height() / 2);

        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
    }


    //-------------------------------------------------------------------------
    private static boolean setLookAndFeel(String plafName)
    {
        try {
            for (UIManager.LookAndFeelInfo info :
                    UIManager.getInstalledLookAndFeels()) {
                if (info.getName().equalsIgnoreCase( plafName )) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }


    private static JComponent buildMainGuiPanel()
    {
        final ZoomingClipper clipper = new ZoomingClipper();
        JComponent clipperFlow = new ScreenClipperFlow( clipper );

        JPanel clipperPanel = new JPanel(new MigLayout());
        clipperPanel.add(clipper, "wrap");
        clipperPanel.add(clipperFlow);

        JButton newFeature = new JButton("New");
        newFeature.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                clipper.allowSizeChange( true );
            }});

        JButton captureFeature = new JButton("Capture");
        captureFeature.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                RgbGrid clip = clipper.clip();
                if (clip == null) {
                    return;
                }
                
                clipper.setClipSize(clip.width(), clip.height());
                clipper.allowSizeChange( false );
                LOG.info("Captured: {}", clip);
            }});

        JPanel controlPanel = new JPanel(new MigLayout());
        controlPanel.add(newFeature, "wrap");
        controlPanel.add(captureFeature, "wrap");
        controlPanel.add(new ClipSizeSelection(clipper));

        JPanel dashPanel = new JPanel(new MigLayout());
        dashPanel.add( controlPanel );
        dashPanel.add( clipperPanel );

        return dashPanel;
    }
}
