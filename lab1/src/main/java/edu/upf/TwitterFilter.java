package edu.upf;

import edu.upf.filter.FileLanguageFilter;
import edu.upf.uploader.S3Uploader;

import java.util.Arrays;
import java.util.List;

public class TwitterFilter {
    public static void main( String[] args ) throws Exception {
        List<String> argsList = Arrays.asList(args);
        String language = argsList.get(0);
        String outputFile = argsList.get(1);
        String bucket = argsList.get(2);
        Integer tweetCount = 0;

        long startTime = System.currentTimeMillis();
        // long elapsedTime = 0L;

        System.out.println("Language: " + language + ". Output file: " + outputFile + ". Destination bucket: " + bucket);
        
        System.out.println("\n=============================== PROCESSING THE FILES ================================\n");
        for(String inputFile: argsList.subList(3, argsList.size())) {
            System.out.println("Processing: " + inputFile);
            long startTimeLocal = System.currentTimeMillis();

            final FileLanguageFilter filter = new FileLanguageFilter(inputFile, outputFile);
            try {
                Integer localCount = filter.filterLanguage(language);
                System.out.println("Filtered " + localCount + " tweets in " + (System.currentTimeMillis() - startTimeLocal) + "ms\n");
                tweetCount += localCount;
            } catch (Exception e) {}
        }

        System.out.println("\n===================================== RESULTS ======================================\n");
        System.out.println("Total number of tweets with language " + language + ": " + tweetCount);
        System.out.println("Total time taken: " + (System.currentTimeMillis() - startTime) + "ms\n");

        final S3Uploader uploader = new S3Uploader(bucket, language, "default");
        uploader.upload(Arrays.asList(outputFile));
    }
}
