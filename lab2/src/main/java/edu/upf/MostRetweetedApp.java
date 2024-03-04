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


public class MostRetweetedApp {
    public static void main(String[] args) {
        List<String> argsList = Arrays.asList(args);
        String outputDir = argsList.get(0);

        //Create a SparkContext to initialize
        SparkConf conf = new SparkConf().setAppName("MostRetweetedApp");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        JavaRDD<String> tweets = sparkContext.emptyRDD();

        // Load input
        for(String inputFile: argsList.subList(1, argsList.size())) {
            tweets = sparkContext.textFile(inputFile).union(tweets);
        }

        // Parsed tweets (non-empty and retweeted ones)
        JavaRDD<Optional<ExtendedSimplifiedTweet>> filteredTweets = tweets
            .map(ExtendedSimplifiedTweet::fromJson)
            .filter(tweet -> !tweet.isEmpty())
            .filter(tweet -> tweet.get().getIsRetweeted());

        // Persist the RDD because we will use it A LOT
        filteredTweets.cache();

        // Retweeted users count
        JavaPairRDD<Long, Long> retweeted_users_count = filteredTweets
            .map(tweet -> tweet.get().getRetweetedUserId())
            .mapToPair(user_id -> new Tuple2<Long, Long>(user_id, 1L))
            .reduceByKey((a, b) -> a + b);

        // Sort descending (most retweeted users first)
        JavaPairRDD<Long, Long> retweeted_users_count_descending = retweeted_users_count
            .mapToPair(tuple -> tuple.swap())
            .sortByKey(false)
            .mapToPair(tuple -> tuple.swap());

        // Retrieve top ten users (list format)
        List<Long> top_ten_users = retweeted_users_count_descending
            .map(user_and_count -> user_and_count._1)
            .take(10);

        // Retweeted tweets count
        JavaPairRDD<Long, Long> retweeted_tweets_count = filteredTweets
            .map(tweet -> tweet.get().getRetweetedId())
            .mapToPair(tweet_id -> new Tuple2<Long, Long>(tweet_id, 1L))
            .reduceByKey((a, b) -> a + b);

        // Sort descending (most retweeted tweets first)
        JavaPairRDD<Long, Long> retweeted_tweets_count_descending = retweeted_tweets_count
            .mapToPair(tuple -> tuple.swap())
            .sortByKey(false)
            .mapToPair(tuple -> tuple.swap());

        // Persist the RDD because we will use it A LOT
        retweeted_tweets_count_descending.cache();
        
        List<Tuple2<Long, Long>> user_tweet = new ArrayList<Tuple2<Long, Long>>();

        for (Long user : top_ten_users) {
            List<Long> user_retweeted_tweets = filteredTweets
                .filter(tweet -> tweet.get().getRetweetedUserId().equals(user))
                .map(tweet -> tweet.get().getRetweetedId()).collect();

            List<Long> most_retweeted_tweet = retweeted_tweets_count_descending
                .filter(tweet_id -> user_retweeted_tweets.contains(tweet_id._1))
                .map(tuple -> tuple._1)
                .take(1);
            
            user_tweet.add(new Tuple2<Long,Long>(user, most_retweeted_tweet.get(0)));
        }

        // Output in array form (ordered)
        System.out.println(user_tweet);

        JavaRDD<Tuple2<Long, Long>> user_tweet_rdd = sparkContext.parallelize(user_tweet);

        user_tweet_rdd.saveAsTextFile(outputDir);

        sparkContext.close();
    }
}
