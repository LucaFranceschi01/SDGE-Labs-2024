ackage spark;

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
        JavaRDD<String> tweet = sparkContext.textFile(input);
       
       
        
        JavaPairRDD<String, Integer> rt_tweets_counter = tweet
            .filter(x -> x.getIsRetweeted().equals(true))
            .mapToPair(rtw -> new Tuple2<>(rtw, 1)) //Map each tweet to a key-value pair
            .reduceByKey((a, b) -> a + b); //Sum values of each key

        //Find the most retweeted users
        JavaPairRDD<String, Integer> top_user_rt = rt_tweets_counter
            .mapToPair(u_rtw -> new Tuple2<>(u_rtw, 1)) //Map each user to a key-value pair
            .reduceByKey((a, b) -> a + b) //Sum values of each key
            .sortByKey(false);

        //System.out.println("Top-10 most retweeted users: " + ));
        .saveAsTextFile(outputDir);
        sparkContext.close();
    }
}
