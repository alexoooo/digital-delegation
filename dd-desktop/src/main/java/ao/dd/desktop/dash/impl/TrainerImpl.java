package ao.dd.desktop.dash.impl;

import ao.dd.desktop.dash.def.Recorder;
import ao.dd.desktop.dash.def.Trainer;
import ao.dd.desktop.dash.model.FeatureDefinition;

import javax.swing.*;

/**
 * User: alex
 * Date: 18-May-2010
 * Time: 11:13:05 PM
 */
public class TrainerImpl
        implements Trainer
{
    //-------------------------------------------------------------------------
    private final FeatureDefinition feature;
    private final Recorder          with;


    //-------------------------------------------------------------------------
    public TrainerImpl(
            FeatureDefinition feature,
            Recorder          with)
    {
        this.feature = feature;
        this.with    = with;
    }


    //-------------------------------------------------------------------------
    @Override
    public JComponent controls()
    {
        return null;
    }
}
