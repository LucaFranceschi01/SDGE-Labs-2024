package edu.upf;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import edu.upf.model.ExtendedSimplifiedTweet;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;
import scala.Tuple2;

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

        for(String inputFile: argsList.subList(1, argsList.size())) {
            // Load input
            JavaRDD<String> tweets = sparkContext.textFile(inputFile);

            // Parsed tweets (non-empty and retweeted ones)
            JavaRDD<Optional<ExtendedSimplifiedTweet>> filteredTweets = tweets
                .map(ExtendedSimplifiedTweet::fromJson)
                .filter(tweet -> !tweet.isEmpty())
                .filter(tweet -> tweet.get().getIsRetweeted());

            // Retweeted users count
            JavaPairRDD<Long, Long> retweeted_users_count = filteredTweets
                .map(tweet -> tweet.get().getRetweetedUserId())
                .mapToPair(user_id -> new Tuple2<Long, Long>(user_id, 1L))
                .reduceByKey((a, b) -> a + b);

            // Sort descending (most retweeted users first)
            JavaPairRDD<Long, Long> retweeted_users_count_descending = retweeted_users_count
                .mapToPair(tuple -> new Tuple2<Long, Long>(tuple._2, tuple._1))
                .sortByKey(false)
                .mapToPair(tuples -> new Tuple2<Long, Long>(tuples._2, tuples._1));

            // Retweeted tweets count
            JavaPairRDD<Long, Long> retweeted_tweets_count = filteredTweets
                .map(tweet -> tweet.get().getRetweetedId())
                .mapToPair(tweet_id -> new Tuple2<Long, Long>(tweet_id, 1L))
                .reduceByKey((a, b) -> a + b);

            // Sort descending (most retweeted tweets first)
            JavaPairRDD<Long, Long> retweeted_tweets_count_descending = retweeted_tweets_count
                .mapToPair(tuple -> new Tuple2<Long, Long>(tuple._2, tuple._1))
                .sortByKey(false)
                .mapToPair(tuples -> new Tuple2<Long, Long>(tuples._2, tuples._1));

            // Retrieve top ten users (list format)
            List<Long> top_ten_users = retweeted_users_count_descending
                .map(user_and_count -> user_and_count._1)
                .take(10);

            // Calculate most retweeted tweet for each of the top ten most retweeted users.
            // for (Long user : top_ten_users) {
            //     JavaPairRDD<Long, Long> most_retweeted_tweet_user = retweeted_users_count
            //         .filter(tweet -> filterTweetsByRetweetedUser(tweet._1, user));
            // }

        }
        
        sparkContext.close();
    }

    public static boolean filterTweetsByRetweetedUser(Optional<ExtendedSimplifiedTweet> tweet, Long retweeted_user) {
        // Get the retweeted user from the tweet
        Long tweet_retweeted_user = tweet.get().getRetweetedUserId();
        Long retweeted_tweet_id = tweet.get().getRetweetedId();
        // Check if the tweet has been retweeted by the user
        if (retweeted_user.equals(tweet_retweeted_user)) {
            return true;
        } else {
            return false;
        }
    }

    /*
    public static boolean fiterTweetsByRetweetedUser(Optional<ExtendedSimplifiedTweet> tweet, List<Long> retweeted_users) {

        // We have previously filtered tweets, so 'retweetedUserId' will be a valid one.
        Long tweet_retweeted_user_id = tweet.get().getRetweetedUserId();

        // Check if the tweet has been retweeted by a user in the list
        if (retweeted_users.contains(tweet_retweeted_user_id)) {
            return true;
        } else {
            return false;
        }
    }
    */

}
