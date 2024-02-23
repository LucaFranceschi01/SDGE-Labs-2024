package edu.upf;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

public class TwitterLanguageFilterApp {
    public static void main( String[] args ){
        List<String> argsList = Arrays.asList(args);
        String language = argsList.get(0);
        String outputFile = argsList.get(1);
        String bucket = argsList.get(2);

        System.out.println(language);
        System.out.println(outputFile);
        System.out.println(bucket);

        //Create a SparkContext to initialize
        SparkConf conf = new SparkConf().setAppName("Twitter Filter");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        sparkContext.close();
    }
}
