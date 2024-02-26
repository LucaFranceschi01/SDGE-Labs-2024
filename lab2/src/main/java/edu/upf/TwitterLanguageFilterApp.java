package edu.upf;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import edu.upf.filter.FileLanguageFilter;

public class TwitterLanguageFilterApp {
    public static void main( String[] args ){
        List<String> argsList = Arrays.asList(args);
        String language = argsList.get(0);
        String outputFile = argsList.get(1);

        //Create a SparkContext to initialize
        SparkConf conf = new SparkConf().setAppName("Twitter Filter");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);

        for(String inputFile: argsList.subList(2, argsList.size())) {
            final FileLanguageFilter filter = new FileLanguageFilter(inputFile, outputFile, sparkContext);
            try {
                Long localCount = filter.filterLanguage(language);
                System.out.println(localCount);
            } catch (Exception e) {}
        }

        sparkContext.close();
    }
}
