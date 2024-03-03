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

        // Retweeted users count
        JavaPairRDD<Long, Long> retweeted_users_count = filteredTweets
            .map(tweet -> tweet.get().getRetweetedUserId())
            .mapToPair(user_id -> new Tuple2<Long, Long>(user_id, 1L))
            .reduceByKey((a, b) -> a + b);

        // Sort descending (most retweeted users first)
        JavaPairRDD<Long, Long> retweeted_users_count_descending = retweeted_users_count
            // .mapToPair(tuple -> new Tuple2<Long, Long>(tuple._2, tuple._1))
            .mapToPair(tuple -> tuple.swap())
            .sortByKey(false)
            .mapToPair(tuple -> tuple.swap());
            // .mapToPair(tuples -> new Tuple2<Long, Long>(tuples._2, tuples._1));

        // Retrieve top ten users (list format)
        List<Long> top_ten_users = retweeted_users_count_descending
            .map(user_and_count -> user_and_count._1)
            .take(10);

        // Retweeted tweets count ((retweetID, authorUserID), count)
        JavaPairRDD<Tuple2<Long, Long>, Long> retweeted_tweets_count = filteredTweets
            .mapToPair(tweet -> new Tuple2<Tuple2<Long, Long>, Long>(
                new Tuple2<Long, Long>(tweet.get().getRetweetedId(), tweet.get().getRetweetedUserId()),
                1L)
            )
            .reduceByKey((a, b) -> a + b);

        // Sort descending (most retweeted tweets first)
        JavaPairRDD<Tuple2<Long, Long>, Long> retweeted_tweets_count_descending = retweeted_tweets_count
            // .mapToPair(tuple -> new Tuple2<Long, Long>(tuple._2, tuple._1))
            .mapToPair(tuple -> tuple.swap())
            .sortByKey(false)
            .mapToPair(tuple -> tuple.swap());
            // .mapToPair(tuples -> new Tuple2<Long, Long>(tuples._2, tuples._1));

        // if we order again by user id we can do a take, parallelize and then finalize (o no, no lo he conseguido)
        // JavaPairRDD<Long, Long> reordering = sparkContext.parallelizePairs(retweeted_tweets_count_descending
        //     .mapToPair(entry -> entry._1.swap())
        //     .sortByKey(false)
        //     .mapToPair(tuple -> tuple.swap())
        //     .take(10)
        // );

        // JavaPairRDD<Long, Long> reordering = retweeted_tweets_count_descending
        //     .mapToPair(entry -> entry._1.swap())
        //     .sortByKey(false)
        //     .mapToPair(tuple -> tuple.swap());

        // JavaPairRDD<Long, Long> last = reordering // FALTA HACER QUE SOLO HAYA UN TWEET CON MAX RETWEETS PER BEST USER
        //     .filter(entry -> top_ten_users.contains(entry._1));

        JavaPairRDD<Long, Long> last = retweeted_tweets_count_descending // FALTA HACER QUE SOLO HAYA UN TWEET CON MAX RETWEETS PER BEST USER
            .filter(entry -> top_ten_users.contains(entry._1._2))
            .mapToPair(entry -> entry._1);

        last.saveAsTextFile(outputDir);

        sparkContext.close();
    }

    // public static boolean filterTweetsByRetweetedUser(Optional<ExtendedSimplifiedTweet> tweet, Long retweeted_user) {
    //     // Get the retweeted user from the tweet
    //     Long tweet_retweeted_user = tweet.get().getRetweetedUserId();
    //     Long retweeted_tweet_id = tweet.get().getRetweetedId();
    //     // Check if the tweet has been retweeted by the user
    //     if (retweeted_user.equals(tweet_retweeted_user)) {
    //         return true;
    //     } else {
    //         return false;
    //     }
    // }

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
