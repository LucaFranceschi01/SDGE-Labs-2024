package spark;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;




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
       

        /*
        //Bigram
        JavaRDD<String> word_list = sentences
            .filter(x -> x.getLanguage().equals(language))
            .flatMap(s -> Arrays.asList(s.split("[ ]")).iterator()) //Split each tweet in words and flatten into a list of words
            .map(word -> normalise(word));

        for (int word = 0; word < length(word_list); word++){
            bigram = new Tuple2<>(word_list[word], word_list[word + 1]);
        } 
        JavaPairRDD<String, String> bigram_count = bigram
            .mapToPair(bigram -> new Tuple2<>(bigram, 1))//Map each bigram to a key-value pair
            .reduceByKey((a, b) -> a + b); //Sum values of each key
        
        //System.out.println("Top-10 most popular bi-grams: " + ));
        JavaPairRDD<List<String>, Integer> top_10 = bigram_count.sortByKey(false);
        bigram_count.saveAsTextFile(outputDir);
        sparkContext.close();
        */
    }

    private static String normalise(String word) {
        return word.trim().toLowerCase(); //Remove word trimming, lower-casing ////should also filter empty words
    }
}


//Word Counter
        /*JavaPairRDD<String, Integer> counts = sentences
            .flatMap(s -> Arrays.asList(s.split("[ ]")).iterator()) //Split each tweet in words and flatten into a list of words
            .map(word -> normalise(word)) //Remove word trimming, lower-casing ////should also filter empty words
            .mapToPair(word -> new Tuple2<>(word, 1)) //Map each word to a key-value pair
            .reduceByKey((a, b) -> a + b); //Sum values of each key
        System.out.println("Total words: " + counts.count());
        counts.saveAsTextFile(outputDir);
        sparkContext.close();*/
