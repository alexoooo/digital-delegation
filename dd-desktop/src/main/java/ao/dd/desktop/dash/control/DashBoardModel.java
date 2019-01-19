package ao.dd.desktop.dash.control;

import ao.dd.desktop.dash.model.FeatureDefinition;
import ao.dd.desktop.dash.model.FeatureDefinitionDao;
import ao.util.async.Publisher;

/**
 * Date: 3/26/11
 * Time: 7:38 PM
 */
public class DashBoardModel
{
    //------------------------------------------------------------------------
    private final Publisher<Listener>  events;
    private final FeatureDefinitionDao featureDefinitionDao;

    private FeatureDefinition feature;


    //------------------------------------------------------------------------
    public DashBoardModel()
    {
        events  = new Publisher<Listener>();
        feature = new FeatureDefinition();

        featureDefinitionDao = FeatureDefinitionDao.defaultInstance;
    }


    //------------------------------------------------------------------------
    public void setFeature(FeatureDefinition feature)
    {
        if (this.feature.equals( feature )) {
            return;
        }

        this.feature = feature;

        events.publish(new Publisher.Distributor<Listener>() {
            @Override public void deliverTo(Listener sub) {
                sub.differentFeatureSelected();
            }});
    }


    //------------------------------------------------------------------------
    public FeatureDefinition activeFeature()
    {
        return feature;
    }


    //------------------------------------------------------------------------
    public Publisher<Listener> events()
    {
        return events;
    }


    //------------------------------------------------------------------------
    public FeatureDefinitionDao dao()
    {
        return featureDefinitionDao;
    }


    //------------------------------------------------------------------------
    public interface Listener
    {
        void differentFeatureSelected();
    }
}
