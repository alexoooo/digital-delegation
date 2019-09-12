package ao.dd.desktop.dash.control.define;

import ao.dd.desktop.dash.control.DashBoardModel;
import ao.dd.desktop.dash.model.FeatureDefinition;
import ao.dd.desktop.dash.model.FeatureDefinitionDao;
import ao.dd.desktop.dash.model.field.BoundArchetypeField;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Date: 3/26/11
 * Time: 7:18 PM
 */
public class DefinePanel
        extends JPanel
{
    //-------------------------------------------------------------------------
    private final DashBoardModel model;
    private final JPanel         sidePanel;
    private final JPanel         mainPanel;


    //-------------------------------------------------------------------------
    public DefinePanel(DashBoardModel model)
    {
        super(new MigLayout("nocache"));
        this.model = model;

        mainPanel = new JPanel();

        model.events().subscribe(new DashBoardModel.Listener() {
            @Override public void differentFeatureSelected() {
                rebuildMainPanel(mainPanel);
            }});
        model.dao().events().subscribe(new FeatureDefinitionDao.Listener() {
            @Override public void featureAddedOrRemoved() {
//                rebuildArchetypeSelection();
                rebuildSidePanel();
            }});

        sidePanel = sidePanel();

        rebuildMainView();
    }


    //-------------------------------------------------------------------------
    private void rebuildMainView()
    {
        removeAll();
        add(sidePanel);

        rebuildMainPanel( mainPanel );
        add(mainPanel, "width 100%, height 100%");

        revalidate();
        repaint();
    }

    private void rebuildArchetypeSelection()
    {
        removeAll();
        add(model.activeFeature().archetype().viewEditable(
                new BoundArchetypeField.Listener() {
                    @Override public void selectedOrCancelled() {
                        rebuildMainView();
                    }}));
        revalidate();
    }


    //-------------------------------------------------------------------------
    private void rebuildSidePanel()
    {
        sidePanel.removeAll();
        addSidePanel(sidePanel);
        revalidate();
    }

    private JPanel sidePanel()
    {
        JPanel p = new JPanel(new MigLayout());
        addSidePanel(p);
        return p;
    }

    private void addSidePanel(JPanel p)
    {
        JButton newButton = new JButton("New");
        newButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                model.setFeature(new FeatureDefinition());
            }});

        final DefaultListModel/*<FeatureDefinition>*/ existingFeatureListModel =
                new DefaultListModel/*<FeatureDefinition>*/();
        final JList/*<FeatureDefinition>*/ existingFeatureList =
                new JList/*<FeatureDefinition>*/( existingFeatureListModel );

        existingFeatureList.addListSelectionListener(
                new ListSelectionListener() {
                    @Override public void valueChanged(ListSelectionEvent e)
                    {
                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        model.setFeature((FeatureDefinition)
                                existingFeatureList.getSelectedValue());
                    }});

        for (FeatureDefinition featureDefinition :
                model.dao().retrieveAll())
        {
            existingFeatureListModel.add(
                    existingFeatureListModel.getSize(),
                    featureDefinition);
        }

        p.add( existingFeatureList, "wrap" );
        p.add( newButton );
    }


    //-------------------------------------------------------------------------
    private void rebuildMainPanel(JPanel p)
    {
        p.removeAll();
        p.setLayout(new MigLayout("nocache"));

        p.add(new JLabel("Name: "));
        p.add(model.activeFeature().name().view(false),
              "wrap");

        p.add(model.activeFeature().archetype().viewReadOnly(),
              "gapy 15, width 100%, height 100%, span, wrap");

        JButton archetypeSelectButton = new JButton("Select Archetype");
        archetypeSelectButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                rebuildArchetypeSelection();
            }});

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                model.dao().store(
                        model.activeFeature());
            }});

        p.add(archetypeSelectButton, "wrap, span");
        p.add(saveButton, "gapy 15, span");

        p.revalidate();
    }
}
