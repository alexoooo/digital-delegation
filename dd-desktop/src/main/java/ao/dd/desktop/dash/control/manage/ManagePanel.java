package ao.dd.desktop.dash.control.manage;

import ao.dd.desktop.dash.control.DashBoardModel;

import javax.swing.*;

/**
 * User: AO
 * Date: 3/26/11
 * Time: 7:19 PM
 */
public class ManagePanel
        extends JPanel
{
    //-------------------------------------------------------------------------
    private final DashBoardModel model;


    //-------------------------------------------------------------------------
    public ManagePanel(DashBoardModel model)
    {
        this.model = model;

        add(new JLabel("Manage captured examples"));
    }
}
