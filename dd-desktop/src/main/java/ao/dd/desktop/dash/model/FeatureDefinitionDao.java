package ao.dd.desktop.dash.model;

import ao.util.async.Publisher;
import ao.util.io.Dirs;

import java.io.File;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * User: AO
 * Date: 3/26/11
 * Time: 8:48 PM
 */
public class FeatureDefinitionDao
{
    //------------------------------------------------------------------------
    private static final File defaultStorageDirectory =
            new File("store-feature");

    public static FeatureDefinitionDao defaultInstance =
            new FeatureDefinitionDao(
                    defaultStorageDirectory);


    //------------------------------------------------------------------------
    private final File                storageDirectory;
    private final Publisher<Listener> events;


    //------------------------------------------------------------------------
    public FeatureDefinitionDao(File storageDirectory)
    {
        this.storageDirectory = storageDirectory;

        events = new Publisher<Listener>();
    }


    //------------------------------------------------------------------------
    public void store(FeatureDefinition featureDefinition)
    {
        File dir = featureDir(featureDefinition);
        //boolean added = ! dir.exists();
        boolean added = ! dir.mkdirs();
        FeatureDefinition.store(
                featureDefinition, dir);
        if (added) {
            events.publish(new FeatureAddedOrRemovedPublication());
        }
    }

    private File featureDir(FeatureDefinition featureDefinition)
    {
        return new File(storageDirectory, featureDefinition.id());
    }

    public void remove(FeatureDefinition featureDefinition)
    {
        if (Dirs.deleteTree( featureDir( featureDefinition ) )) {
            events.publish(new FeatureAddedOrRemovedPublication());
        }
    }


    //------------------------------------------------------------------------
    public List<FeatureDefinition> retrieveAll()
    {
        List<FeatureDefinition> definitions = newArrayList();

        for (File f : storageDirectory.listFiles()) {
            if (! f.isDirectory()) {
                continue;
            }

            FeatureDefinition definition =
                    FeatureDefinition.retrieve( f );
            if (definition != null) {
                definitions.add( definition );
            }
        }

        return definitions;
    }


    //------------------------------------------------------------------------
    public List<FeatureExample> retrieveAllExamples(
            String featureDefinitionId)
    {
        return null;
    }

    public void storeExample(FeatureExample featureExample)
    {

    }

    public void removeExample(FeatureExample featureExample)
    {

    }


    //------------------------------------------------------------------------
    public Publisher<Listener> events()
    {
        return events;
    }


    //------------------------------------------------------------------------
    public interface Listener
    {
        void featureAddedOrRemoved();
    }

    private static class FeatureAddedOrRemovedPublication
            implements Publisher.Distributor<Listener> {
        @Override public void deliverTo(Listener subscriber) {
            subscriber.featureAddedOrRemoved();
        }
    }
}

