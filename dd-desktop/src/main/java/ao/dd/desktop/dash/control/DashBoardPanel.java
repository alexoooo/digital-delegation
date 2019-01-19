package ao.dd.desktop.dash.control;

import ao.dd.desktop.dash.control.capture.CapturePanel;
import ao.dd.desktop.dash.control.define.DefinePanel;
import ao.dd.desktop.dash.control.manage.ManagePanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

/**
 * User: AO
 * Date: 3/26/11
 * Time: 7:16 PM
 */
public class DashBoardPanel
        extends JPanel
{
    //------------------------------------------------------------------------
    private final DashBoardModel model;

    private final JPanel header;


    //------------------------------------------------------------------------
    public DashBoardPanel()
    {
        super(new MigLayout());

        model = new DashBoardModel();

        header = new JPanel();
        rebuildHeader( header );

        model.events().subscribe(new DashBoardModel.Listener() {
            @Override public void differentFeatureSelected() {
                rebuildHeader( header );
            }});

        add(header, "width 100%, wrap");
        add(buildMain(), "width 100%");
    }


    //------------------------------------------------------------------------
    private void rebuildHeader(JPanel header)
    {
        header.removeAll();
        header.setLayout(new MigLayout());

//        System.out.println("rebuilding header with" + model.activeFeature().name().get());
        header.add(new JLabel("Active component: "));
        header.add( model.activeFeature().name().view(true) );

//        revalidate();
        header.repaint();
    }

    private JComponent buildMain()
    {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Define" , new DefinePanel ( model ));
        tabs.addTab("Capture", new CapturePanel( model ));
        tabs.addTab("Manage" , new ManagePanel ( model ));

        return tabs;
    }
}
