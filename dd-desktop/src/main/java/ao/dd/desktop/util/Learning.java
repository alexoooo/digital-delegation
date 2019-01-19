package ao.dd.desktop.util;

//import ao.ai.classify.decision.MinInfoBinClassifier;
import ao.ai.model.ml.classify.example.ext.NumBinClassified;
import ao.ai.model.ml.classify.example.impl.XyBinClassified;
import ao.util.math.rand.Rand;

/**
 * User: Eugene
 * Date: May 2, 2010
 * Time: 5:52:05 PM
 */
public class Learning
{
//    //-------------------------------------------------------------------------
//    public static void main(String[] args)
//    {
//        List<NumBinClassified> examples =
//                Lists.newArrayList();
//
//        for (int i = 0; i < 100; i++)
//        {
//            examples.add(generateExample(100 , 100 , true));
//            examples.add(generateExample(1000, 1000, false));
//        }
//
//        NumBinProbClassLeaner learner =
//                new MinInfoBinClassifier();
//
//        NumBinProbClassifier classifier =
//                learner.learn( examples );
//
//        System.out.println(
//                classifier.classify(new IntNumList(0, 0)));
//        System.out.println(
//                classifier.classify(new IntNumList(1500, 1500)));
//    }


    //-------------------------------------------------------------------------
    private static NumBinClassified generateExample(
            int aroundX, int aroundY, boolean isPositive)
    {
        int realX = (int)(aroundX * Rand.nextDouble(0.9, 1.1));
        int realY = (int)(aroundY * Rand.nextDouble(0.9, 1.1));

        return new XyBinClassified(
                realX, realY, isPositive);
    }
}
