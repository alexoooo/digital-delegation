package ao.dd.desktop.dash.capture.attrib;

import ao.dd.desktop.dash.capture.Clipper;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * User: AO
 * Date: Aug 11, 2010
 * Time: 3:46:55 PM
 */
public class ClipSizeSelection
        extends JPanel
{
    //------------------------------------------------------------------------
    private final Clipper clipper;
//    private final Document widthDoc;
//    private final Document heightDoc;
    private final JLabel   widthText;
    private final JLabel   heightText;


    //------------------------------------------------------------------------
    public ClipSizeSelection(Clipper clipperToWatch)
    {
        this.clipper = clipperToWatch;
        setLayout(new MigLayout());

        widthText  = new JLabel();
        heightText = new JLabel();

        add(new JLabel("Width: "));
        add(widthText, "w 30!");
        add(new JLabel("px"), "wrap");

        add(new JLabel("Height: "));
        add(heightText);
        add(new JLabel("px"));

        clipper.addListener(new Clipper.Listener() {
            @Override public void selectionChanged(
                    Rectangle selection)
            {
                if (selection == null)
                {
                    widthText .setText("");
                    heightText.setText("");
                }
                else
                {
                    widthText .setText(String.valueOf(
                            selection.width ) );
                    heightText.setText(String.valueOf(
                            selection.height) );
                }
            }});
    }
}
