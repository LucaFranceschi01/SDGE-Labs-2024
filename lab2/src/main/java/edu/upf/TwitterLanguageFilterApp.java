package edu.upf;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import edu.upf.model.SimplifiedTweet;

public class TwitterLanguageFilterApp {
    public static void main( String[] args ){
        List<String> argsList = Arrays.asList(args);
        String language = argsList.get(0);
        String outputFile = argsList.get(1);


        long startTime = System.currentTimeMillis();
        // long elapsedTime = 0L;

        System.out.println("Language: " + language + ". Output folder: " + outputFile);
        
        System.out.println("\n=============================== PROCESSING THE FILES ================================\n");

        //Create a SparkContext to initialize
        SparkConf conf = new SparkConf().setAppName("Twitter Filter");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        JavaRDD<String> tweets = sparkContext.emptyRDD();

        for(String inputFile: argsList.subList(2, argsList.size())) {
            tweets = sparkContext.textFile(inputFile).union(tweets);
        }

        JavaRDD<Optional<SimplifiedTweet>> filteredTweets = tweets
            .map(SimplifiedTweet::fromJson)
            .filter(tweet -> !tweet.isEmpty())
            .filter(tweet -> (tweet.get().getLanguage()).equals(language));
        
        JavaRDD<String> output = filteredTweets.map(tweet -> tweet.toString());

        output.saveAsTextFile(outputFile);

        System.out.println("\n===================================== RESULTS ======================================\n");
        System.out.println("Total number of tweets with language " + language + ": " + output.count());
        System.out.println("Total time taken: " + (System.currentTimeMillis() - startTime) + "ms\n");

        sparkContext.close();
    }
}
