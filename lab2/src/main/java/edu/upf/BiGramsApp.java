package edu.upf;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import edu.upf.model.ExtendedSimplifiedTweet;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BiGramsApp {
    public static void main(String[] args){
        List<String> argsList = Arrays.asList(args);
        String language = argsList.get(0);
        String outputDir = argsList.get(1);

        //Create a SparkContext to initialize
        SparkConf conf = new SparkConf().setAppName("BiGramsApp");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        JavaRDD<String> tweets = sparkContext.emptyRDD();

        for(String inputFile: argsList.subList(2, argsList.size())) {
            // Load input
            tweets = sparkContext.textFile(inputFile).union(tweets);
        }

        // Parsed tweets
        JavaRDD<Optional<ExtendedSimplifiedTweet>> filteredTweets = tweets
            .map(ExtendedSimplifiedTweet::fromJson)
            .filter(tweet -> !tweet.isEmpty())
            .filter(tweet -> (tweet.get().getLanguage()).equals(language));
        
        // Create a key-value RDD where key is the bigram in the form Tuple2<word1, word2> and value is times they appear
        JavaPairRDD<Tuple2<String, String>, Long> bigrams = filteredTweets
            .map(tweet -> tweet.get().getText())
            .flatMap(text -> bigramsFromText(text).iterator())
            .mapToPair(bigram -> new Tuple2<Tuple2<String, String>, Long>(bigram, 1L))
            .reduceByKey((a, b) -> a + b);

        // Get bigrams ordered by descending frequency
        JavaPairRDD<Tuple2<String, String>, Long> bigrams_descending_frequency = bigrams
            .mapToPair(tuple -> new Tuple2<Long, Tuple2<String, String>>(tuple._2, tuple._1))
            .sortByKey(false)
            .mapToPair(tuples -> new Tuple2<Tuple2<String, String>, Long>(tuples._2, tuples._1));

        bigrams_descending_frequency.saveAsTextFile(outputDir);
        
        sparkContext.close();
    }

    private static String normalise(String word) {
        return word.trim().toLowerCase(); //Word trimming, lower-casing
    }

    private static List<Tuple2<String, String>> bigramsFromText(String text) {
        //List of bigrams
        List<Tuple2<String, String>> bigrams = new ArrayList<Tuple2<String, String>>();
        
        // Get array of words
        List<String> words = Arrays.asList(text.split(" "));

        // Create bigrams (normalising first) and adding them to the bigram list
        for (int ind = 0; ind < words.size() - 2; ind++) {
            if (!normalise(words.get(ind)).isEmpty() && !normalise(words.get(ind+1)).isEmpty()) {
                bigrams.add(new Tuple2<String,String>(normalise(words.get(ind)), normalise(words.get(ind+1))));
            }
        }

        return bigrams;
    }
}