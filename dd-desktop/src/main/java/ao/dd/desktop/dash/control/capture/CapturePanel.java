package ao.dd.desktop.dash.control.capture;

import ao.dd.desktop.dash.control.DashBoardModel;

import javax.swing.*;

/**
 * User: AO
 * Date: 3/26/11
 * Time: 7:19 PM
 */
public class CapturePanel
        extends JPanel
{
    //-------------------------------------------------------------------------
    private final DashBoardModel model;


    //-------------------------------------------------------------------------
    public CapturePanel(DashBoardModel model)
    {
        this.model = model;

        add(new JLabel("Capture new examples"));
    }
}
