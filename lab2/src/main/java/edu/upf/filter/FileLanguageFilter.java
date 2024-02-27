package edu.upf.filter;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.util.SizeEstimator;

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
        System.out.println("Lines:" + tweets.count());
        JavaRDD<Optional<SimplifiedTweet>> filteredTweets = tweets
            .map(SimplifiedTweet::fromJson);
            //.filter(tweet -> !tweet.isEmpty())
            //.filter(tweet -> (tweet.get().getLanguage()).equals(language));
        
        //System.out.println("Language filtered:" + filteredTweets.count());

        JavaRDD<String> output = filteredTweets.map(tweet -> tweet.toString());

        System.out.println("filters");
        //String path = outputFile + "/out.json";
        //System.out.println(output.count());
        output.saveAsTextFile(outputFile);
        System.out.println("saves");

        Long counter = SizeEstimator.estimate(filteredTweets);
        System.out.println(counter);

        return counter;
    }
}