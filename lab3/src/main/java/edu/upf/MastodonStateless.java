package edu.upf;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import com.github.tukaaa.MastodonDStream;
import com.github.tukaaa.config.AppConfig;
import com.github.tukaaa.model.SimplifiedTweetWithHashtags;

import edu.upf.util.LanguageMapUtils;

@SuppressWarnings("deprecation")
public class MastodonStateless {
    public static void main(String[] args) {
        String input = args[0];

        if (args.length != 1) {
            System.err.println("Usage: MastodonStateless <language mapping tsv>");
            System.exit(1);
        }

        SparkConf conf = new SparkConf().setAppName("Real-time Twitter Stateless Exercise");
        AppConfig appConfig = AppConfig.getConfig();

        StreamingContext ssc = new StreamingContext(conf, Durations.seconds(20));
        JavaStreamingContext jsc = new JavaStreamingContext(ssc);
        jsc.checkpoint("/tmp/checkpoint");

        JavaDStream<SimplifiedTweetWithHashtags> stream = new MastodonDStream(ssc, appConfig).asJStream();

        JavaRDD<String> lines = jsc.sparkContext().textFile(input);
        JavaPairRDD<String, String> language_map = LanguageMapUtils.buildLanguageMap(lines);

        language_map.cache();   // persist

        JavaPairDStream<String, Long> languages_times = stream
            .map(tweet -> tweet.getLanguage())
            .mapToPair(lang -> new Tuple2<String, Long>(lang, 1L))      // emit intermediate (lang, 1)
            .reduceByKey((a, b) -> a + b)                               // reduce (lang, count)
            .transformToPair(rdd -> rdd.join(language_map))             // join with static rdd (lang, (count, language)))
            .mapToPair(entry -> entry._2)                               // remove lang (count, language)
            .transformToPair(rdd -> rdd.sortByKey(false))               // sort descending
            .mapToPair(tuple -> tuple.swap());                          // swap (language, count)

        languages_times.print();

        // Start the application and wait for termination signal
        ssc.start();
        ssc.awaitTermination();
        jsc.close();
    }
}