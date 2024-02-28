package edu.upf;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;


public class MostRetweetedApp {
    public static void main(String[] args){
        String outputDir = args[0];
        String input = args[1];

        //Create a SparkContext to initialize
        SparkConf conf = new SparkConf().setAppName("MostRetweetedApp");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        // Load input
        JavaRDD<String> tweets = sparkContext.textFile(input);
       
       
        /*
       
        //Find the most retweeted users
        JavaPairRDD<Long, Integer> top_userretweets = tweets
            .mapToPair(u_rtw -> new Tuple2<>(u_rtw.getRetweetedUserId(), 1)) //Map each user to a key-value pair
            .reduceByKey((a, b) -> a + b); //Sum values of each key
            //.sortByKey(false);

        //Find the most retweeted tweets
        JavaPairRDD<String, Integer> top_retweets = tweets
            .filter(x -> x.getIsRetweeted().equals(true))
            .mapToPair(rtw -> new Tuple2<>(rtw, 1)) //Map each tweet to a key-value pair
            .reduceByKey((a, b) -> a + b); //Sum values of each key

        //Find the mst retweeted tweet for the 10 most retweeted users (one per user)
        JavaPairRDD<Long, Integer> top = top_retweets.join(top_userretweets)
            .sortByKey(false);


        //System.out.println("Top-10 most retweeted users: " + ));
        top.saveAsTextFile(outputDir);
        sparkContext.close();
        */
    }
}
