package spark;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;
import scala.Tuple2;

import java.util.Arrays;
import edu.upf.model.*;




public class BiGramsApp {
    public static void main(String[] args){
        String language = args[0];
        String outputDir = args[1];
        String input = args[2];

        //Create a SparkContext to initialize
        SparkConf conf = new SparkConf().setAppName("BiGramsApp");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        // Load input
        JavaRDD<String> sentences = sparkContext.textFile(input);

        
        //Word Counter
        /*JavaPairRDD<String, Integer> counts = sentences
            .flatMap(s -> Arrays.asList(s.split("[ ]")).iterator()) //Split each tweet in words and flatten into a list of words
            .map(word -> normalise(word)) //Remove word trimming, lower-casing ////should also filter empty words
            .mapToPair(word -> new Tuple2<>(word, 1)) //Map each word to a key-value pair
            .reduceByKey((a, b) -> a + b); //Sum values of each key
        System.out.println("Total words: " + counts.count());
        counts.saveAsTextFile(outputDir);
        sparkContext.close();*/

        
        //Bigram Counter
        JavaPairRDD<String, String, Integer> counts = sentences
            .flatMap(s -> Arrays.asList(s.split("[ ]")).iterator()) //Split each tweet in words and flatten into a list of words
            .map(word -> normalise(word))
            .filter(w -> w.getLanguage().equals(language))
            .mapToPair(idx -> new Tuple2<>(idx, idx+1)) 
            .mapToPair(bigram -> new Tuple2<>(bigram, 1))//Map each bigram to a key-value pair
            .reduceByKey((a,b) -> a+b); //Sum values of each key

        counts.saveAsTextFile(outputDir);
        sparkContext.close();
    }

    private static String normalise(String word) {
        return word.trim().toLowerCase(); //Remove word trimming, lower-casing ////should also filter empty words
    }
}
