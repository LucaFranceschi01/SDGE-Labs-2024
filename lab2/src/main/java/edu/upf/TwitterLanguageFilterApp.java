package edu.upf;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import edu.upf.filter.FileLanguageFilter;

public class TwitterLanguageFilterApp {
    public static void main( String[] args ){
        List<String> argsList = Arrays.asList(args);
        String language = argsList.get(0);
        String outputFile = argsList.get(1);

        Long tweetCount = 0L;

        long startTime = System.currentTimeMillis();
        // long elapsedTime = 0L;

        System.out.println("Language: " + language + ". Output folder: " + outputFile);
        
        System.out.println("\n=============================== PROCESSING THE FILES ================================\n");

        //Create a SparkContext to initialize
        SparkConf conf = new SparkConf().setAppName("Twitter Filter");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);

        for(String inputFile: argsList.subList(2, argsList.size())) {
            System.out.println("Processing: " + inputFile);
            long startTimeLocal = System.currentTimeMillis();

            final FileLanguageFilter filter = new FileLanguageFilter(inputFile, outputFile, sparkContext);
            try {
                Long localCount = filter.filterLanguage(language);
                System.out.println("Filtered " + localCount + " tweets in " + (System.currentTimeMillis() - startTimeLocal) + "ms\n");
                tweetCount += localCount;
            } catch (Exception e) {}
        }

        System.out.println("\n===================================== RESULTS ======================================\n");
        System.out.println("Total number of tweets with language " + language + ": " + tweetCount);
        System.out.println("Total time taken: " + (System.currentTimeMillis() - startTime) + "ms\n");

        sparkContext.close();
    }
}
