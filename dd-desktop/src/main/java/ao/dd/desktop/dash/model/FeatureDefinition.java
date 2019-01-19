package ao.dd.desktop.dash.model;

import ao.dd.desktop.dash.model.field.BoundArchetypeField;
import ao.dd.desktop.dash.model.field.BoundTextField;
import ao.util.misc.Uuid;
import ao.util.persist.PersistentChars;

import java.io.File;

/**
 * User: alex
 * Date: 18-May-2010
 * Time: 10:50:56 PM
 */
public class FeatureDefinition
{
    //-------------------------------------------------------------------------
    private final String              id;
    private final BoundTextField      name;
    private final BoundArchetypeField archetype;


    //-------------------------------------------------------------------------
    public FeatureDefinition()
    {
        id        = new Uuid().toString();
        name      = new BoundTextField("New Feature");
        archetype = new BoundArchetypeField();
    }

    private FeatureDefinition(
            String              id,
            BoundTextField      name,
            BoundArchetypeField archetype)
    {
        this.id        = id;
        this.name      = name;
        this.archetype = archetype;
    }


    //-------------------------------------------------------------------------
    public String id()
    {
        return id;
    }


    //-------------------------------------------------------------------------
    public BoundTextField name()
    {
        return name;
    }

    public BoundArchetypeField archetype()
    {
        return archetype;
    }


    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return name.get();
    }


    //-------------------------------------------------------------------------
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeatureDefinition that = (FeatureDefinition) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }


    //-------------------------------------------------------------------------
    public static void store(
            FeatureDefinition value, File dir)
    {
        PersistentChars.persist(
                value.id().toCharArray(),
                new File(dir, "id.txt"));

        PersistentChars.persist(
                value.name().get().toCharArray(),
                new File(dir, "name.txt"));

        value.archetype().write(dir);
    }

    public static FeatureDefinition retrieve(File dir)
    {
        String id = new String(PersistentChars.retrieve(
                new File(dir, "id.txt")));

        String name = new String(PersistentChars.retrieve(
                new File(dir, "name.txt")));

        BoundArchetypeField archetype =
                BoundArchetypeField.read( dir );

        return new FeatureDefinition(
                id,
                new BoundTextField( name ),
                archetype);
    }
}
