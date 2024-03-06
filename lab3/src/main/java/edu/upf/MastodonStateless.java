package edu.upf;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
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

public class MastodonStateless {
    @SuppressWarnings("deprecation") // para que no se llene de errores pero se puede quitar
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

        // TODO IMPLEMENT ME
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> lines = sc.textFile(input);
        JavaPairRDD<String, String> language_map = LanguageMapUtils.buildLanguageMap(lines);

        language_map.cache();

        // language_map.saveAsTextFile("./testing");

        JavaPairDStream<String, Long> languages_times = stream
            .map(tweet -> tweet.getLanguage())
            .mapToPair(lang -> new Tuple2<String, Long>(lang, 1L))
            .reduceByKey((a, b) -> a + b)
            .mapToPair(line -> new Tuple2<String, Long>(language_map.lookup(line._1).get(1), line._2));
            // .transformToPair(rdd -> rdd.join(language_map));

        // Start the application and wait for termination signal
        ssc.start();
        ssc.awaitTermination();
        sc.close();
    }
}