package ao.dd.desktop.util;

import ao.dd.desktop.model.image.RgbGrid;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;
import java.awt.Color;
import java.awt.Point;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * User: Eugene
 * Date: May 4, 2010
 * Time: 10:43:07 PM
 */
public class ColourMap
{
    //-------------------------------------------------------------------------
    private final Multimap<Color, Point> colourMap;
    private final List<Color> byPointCountDescending;


    //-------------------------------------------------------------------------
    public ColourMap( RgbGrid colorGrid )
    {
        colourMap = ArrayListMultimap.create();

        for (int x = 0; x < colorGrid.width(); x++)
        {
            for(int y = 0; y < colorGrid.height(); y++)
            {
                colourMap.put(
                        colorGrid.colour(x, y),
                        new Point(x, y));
            }
        }

        byPointCountDescending = sortByCountDescending( this );
    }


    //-------------------------------------------------------------------------
    public ColourMap( Color[] colours )
    {
//        this(new ImageRgbGrid( colours ));

        colourMap = ArrayListMultimap.create();

        for (int x = 0; x < colours.length; x++)
        {
            colourMap.put(
                    colours[ x ],
                    new Point(x, 0));
        }

        byPointCountDescending = sortByCountDescending( this );
    }


    //-------------------------------------------------------------------------
    public Color leastFrequent ()
    {
//        return colours().get( colours().size() - 1);
        return byPointCountDescending.get( colours().size() - 1);
    }


    //-------------------------------------------------------------------------
    public Collection<Point> points( Color colour )
    {
        return colourMap.get( colour );
    }


    //-------------------------------------------------------------------------
    public List<Color> colours()
    {
        return Lists.newArrayList( colourMap.keySet() );
    }

    public boolean isEmpty()
    {
        return colourMap.isEmpty();
    }

    public int size()
    {
        return colourMap.size();
    }


    //-------------------------------------------------------------------------    
    public void remove( Color c, Point p )
    {
        colourMap.remove(c, p);
    }


    //-------------------------------------------------------------------------
    public double purity( int numOfColoursToConsider )
    {
        if ( isEmpty() ) return -1;

//        Collection<Color> colours = this.colours();

        if ( colourMap.keySet().size()
                <= numOfColoursToConsider )
        {
            return  1;
        }

        double pure = 0;
        for (int i = 0; i < numOfColoursToConsider; i++)
        {
            pure += points( byPointCountDescending.get(i) ).size();
        }

        return pure / colourMap.size();
    }


    //-------------------------------------------------------------------------
    public double colourRatio ()
    {
        if ( colours().size() < 2 ) return Double.NaN;

        return (( double )
                 points( byPointCountDescending.get( 1 )).size()
               / points( byPointCountDescending.get( 0 )).size());
    }


    //-------------------------------------------------------------------------
    private static List<Color> sortByCountDescending(
            final ColourMap toSort)
    {
        List<Color> colours = Lists.newArrayList(
                toSort.colours());

        Collections.sort(
                colours,
                new Comparator<Color>() {
                    @Override public int compare(Color a, Color b) {
                        return -Ints.compare(
                                  toSort.colourMap.get(a).size(),
                                  toSort.colourMap.get(b).size());
                    }
                }
        );

        return colours;
    }


    //-------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "size: " + colours().size();
    }
}
