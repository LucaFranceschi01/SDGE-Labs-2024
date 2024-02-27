package edu.upf.filter;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.util.SizeEstimator;

import edu.upf.model.*;

public class FileLanguageFilter implements LanguageFilter {
    final String inputFile;
    final String outputFile;
    final JavaSparkContext jsc;

    public FileLanguageFilter(String inputFile, String outputFile, JavaSparkContext jsc) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.jsc = jsc;
    }

    @Override
    public Long filterLanguage(String language) throws Exception {
        
        System.out.println("entrance");
        JavaRDD<String> tweets = jsc.textFile(inputFile);
        System.out.println("step1");
        JavaRDD<String> filteredTweets = tweets.filter(tweet -> SimplifiedTweet.fromJson(tweet).get().getLanguage().equals(language));
        System.out.println("step2");
        filteredTweets.saveAsTextFile(outputFile);
        System.out.println("step3");
        
        Long counter = SizeEstimator.estimate(filteredTweets);
        System.out.println(counter);

        return counter;
    }
}