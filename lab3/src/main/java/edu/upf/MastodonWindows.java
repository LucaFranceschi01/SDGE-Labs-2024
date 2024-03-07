package edu.upf;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import com.github.tukaaa.MastodonDStream;
import com.github.tukaaa.config.AppConfig;
import com.github.tukaaa.model.SimplifiedTweetWithHashtags;

import edu.upf.util.LanguageMapUtils;
import scala.Tuple2;

@SuppressWarnings("deprecation")
public class MastodonWindows {
    public static void main(String[] args) {
        String input = args[0];

        SparkConf conf = new SparkConf().setAppName("Real-time Mastodon Stateful with Windows Exercise");
        AppConfig appConfig = AppConfig.getConfig();

        StreamingContext sc = new StreamingContext(conf, Durations.seconds(20));
        JavaStreamingContext jsc = new JavaStreamingContext(sc);
        jsc.checkpoint("/tmp/checkpoint");

        // TODO IMPLEMENT ME
        JavaRDD<String> lines = jsc.sparkContext().textFile(input);
        JavaPairRDD<String, String> language_map = LanguageMapUtils.buildLanguageMap(lines);
        language_map.cache();   // persist
        
        JavaDStream<SimplifiedTweetWithHashtags> stream = new MastodonDStream(sc, appConfig).asJStream();
        JavaDStream<SimplifiedTweetWithHashtags> windowed_stream = stream.window(Durations.seconds(60));

        JavaPairDStream<Long, String> batch_languages = stream
            .map(tweet -> tweet.getLanguage())
            .mapToPair(lang -> new Tuple2<String, Long>(lang, 1L))      // emit intermediate (lang, 1)
            .reduceByKey((a, b) -> a + b)                               // reduce (lang, count)
            .transformToPair(rdd -> rdd.join(language_map))             // join with static rdd (lang, (count, language)))
            .mapToPair(entry -> entry._2)                               // remove lang (count, language)
            .transformToPair(rdd -> rdd.sortByKey(false));               // sort descending
        
        batch_languages.print(15);

        JavaPairDStream<Long, String> windowed_languages = windowed_stream
            .map(tweet -> tweet.getLanguage())
            .mapToPair(lang -> new Tuple2<String, Long>(lang, 1L))      // emit intermediate (lang, 1)
            .reduceByKey((a, b) -> a + b)                               // reduce (lang, count)
            .transformToPair(rdd -> rdd.join(language_map))             // join with static rdd (lang, (count, language)))
            .mapToPair(entry -> entry._2)                               // remove lang (count, language)
            .transformToPair(rdd -> rdd.sortByKey(false));               // sort descending

        windowed_languages.print(15);

        // Start the application and wait for termination signal
        sc.start();
        sc.awaitTermination();
        jsc.close();
    }
}