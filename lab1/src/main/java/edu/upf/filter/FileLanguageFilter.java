package edu.upf.filter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Optional;

import edu.upf.parser.*;

public class FileLanguageFilter implements LanguageFilter {
    final String inputFile;
    final String outputFile;

    public FileLanguageFilter(String infile, String ofile) {
        this.inputFile = infile;
        this.outputFile = ofile;
    }

    @Override
    public void filterLanguage(String language) throws Exception {
        // append to ofile the tweets from infile from a language

        try {
            FileReader reader = new FileReader(inputFile);
            BufferedReader bReader = new BufferedReader(reader);

            FileWriter writer = new FileWriter(outputFile);
            BufferedWriter bWriter = new BufferedWriter(writer);
            
            String line, lang;
            Optional<SimplifiedTweet> opTweet;
            SimplifiedTweet tweet;

            do {
                line = bReader.readLine();

                opTweet = SimplifiedTweet.fromJson(line);

                if (opTweet.isEmpty()) {
                    continue;
                }

                tweet = opTweet.get();
                lang = tweet.getLanguage();

                if (lang.equals(language)) {
                    bWriter.write(line); // line or tweet.toString() ???
                }
            } while (line != null);

            bWriter.close();
            bReader.close();
        } 
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        };
    }
}
