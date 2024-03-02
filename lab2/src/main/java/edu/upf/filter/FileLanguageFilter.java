package edu.upf.filter;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import edu.upf.model.*;

import java.util.Optional;

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
        JavaRDD<String> tweets = jsc.textFile(inputFile);

        JavaRDD<Optional<SimplifiedTweet>> filteredTweets = tweets
            .map(SimplifiedTweet::fromJson)
            .filter(tweet -> !tweet.isEmpty())
            .filter(tweet -> (tweet.get().getLanguage()).equals(language));
        
        JavaRDD<String> output = filteredTweets.map(tweet -> tweet.toString());

        Long counter = output.count();

        // not a big fan, remove functionality
        // String[] splitted = inputFile.split("/");
        // String subfolder = "/" + splitted[splitted.length-1];

        // output.saveAsTextFile(outputFile + subfolder);
        output.saveAsTextFile(outputFile);

        return counter;
    }
}