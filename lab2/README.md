# Lab 2
<font color="blue"><b>Authors: Luca Franceschi, Candela Álvarez, Alejandro González</b></font>

> [!TIP]
> We encourage reading this report in the Preview format instead of the Raw format for better visualization.

This report is divided into 6 sections:

- [Delivery and S3 structure](#delivery-and-s3-structure)

- [Changes with respect to last lab](#changes-with-respect-to-last-lab)
    1. [Return to deprecated functions of `JsonParser`](#return-to-deprecated-functions-of-jsonparser)
    2. [No usage of `LanguageFilter` nor `FileLanguageFilter` classes](#no-usage-of-languagefilter-nor-filelanguagefilter-classes)

- [Spark-based TwitterFilter locally](#spark-based-twitterfilter-locally)
    1. [Program execution](#program-execution)
    2. [Local benchmarking](#local-benchmarking)

- [Spark-based TwitterFilter application on EMR](#spark-based-twitterfilter-application-on-emr)
    1. [Cluster execution](#cluster-execution)
    2. [EMR benchmarking](#emr-benchmarking)

- [Most popular bi-grams in a given language](#most-popular-bi-grams-in-a-given-language)
    1. [Approach](#bi-grams-approach)
    2. [Results](#bi-grams-results)

- [Most Retweeted Tweets for Most Retweeted Users](#most-retweeted-tweets-for-most-retweeted-users)
    1. [Approach](#approach)
    2. [Results](#results)

## Delivery and S3 structure

The delivered LSDS-G8-lab2.zip file should be structured as follows:

```bash
LSDS-G8-lab2.zip
├── pom.xml
├── README.md
├── src
│   └── main
│       └── java
│           └── edu
│               └── upf
│                   ├── model
│                   │   ├── ExtendedSimplifiedTweet.java
│                   │   └── SimplifiedTweet.java
│                   ├── BiGramsApp.java
│                   ├── MostRetweetedApp.java
│                   └── TwitterLanguageFilterApp.java
└── target
    └── spark-twitter-filter-1.0-SNAPSHOT.jar

7 directories, 8 files
```

> [!NOTE]
> Due to an inconsistency in the handout we did not know where to place BigramsApp.java but we believe it should be placed inside `edu.upf` directory.

The S3 bucket is structured as:

```bash
lsds2024.lab2.output.u198741
├── input
    ├── Eurovision3.json
    ├── Eurovision4.json
    ├── Eurovision5.json
    ├── Eurovision6.json
    ├── Eurovision7.json
    ├── Eurovision8.json
    ├── Eurovision9.json
    └── Eurovision10.json
├── jars
│   └── spark-twitter-filter-1.0-SNAPSHOT.jar
└──  output
   └── benchmark
       ├── es
       ├── hu
       └── pt

4 directories, 9 files
```

> [!NOTE]
> In this tree we are not considering all files inside each of the languages in the `output/benchmark` folder because they are a lot.

## Changes with respect to last lab

Here are the changes we made with respect to Lab 1.

### Return to deprecated functions of `JsonParser`

We were having some issues with the dependencies on the `pom.xml` file (more precisely an error called `NoSuchMethodError`) because it didn't detect the version of the `GSON` library we were using (`2.8.6`), so the function `parseString(String jsonStr)` didn't exist.

We fixed this by returning to the deprecated functions. The changes have affected [SimplifiedTweet](./src/main/java/edu/upf/model/SimplifiedTweet.java) and [ExtendedSimplifiedTweet](./src/main/java/edu/upf/model/ExtendedSimplifiedTweet.java) classes. Now the code looks like:

```java
JsonParser parser = new JsonParser();
JsonObject jsonObject = parser.parse(jsonStr).getAsJsonObject();
```

### No usage of `LanguageFilter` nor `FileLanguageFilter` classes

As we wanted to analyze all files together when given more than one `.json` file as input, and we are working with RDDs, we had to change the way we filtered each file. Before it was enough to filter separately each file and append the tweets to the output file. Now, we needed to first join all tweets from all files in a single RDD and filter it.

Therefore, the code that creates the joint RDD is:

```java
JavaRDD<String> tweets = sparkContext.emptyRDD();

for(String inputFile: argsList.subList(2, argsList.size())) {
    tweets = sparkContext.textFile(inputFile).union(tweets);
}
```

This way, as we process all the tweets together and just once, we do not need neither the `LanguageFilter` nor the `FileLanguageFilter` classes, so we removed them from the `src` folder. The way we filter now is:

```java
JavaRDD<Optional<SimplifiedTweet>> filteredTweets = tweets
    .map(SimplifiedTweet::fromJson)
    .filter(tweet -> !tweet.isEmpty())
    .filter(tweet -> (tweet.get().getLanguage()).equals(language));
        
JavaRDD<String> output = filteredTweets.map(tweet -> tweet.toString());

output.saveAsTextFile(outputFile);
```

## Spark-based TwitterFilter locally

Here we explain how to execute the program locally and show results obtained.

### Program execution

We can run the [TwitterLanguageFilterApp](./src/main/java/edu/upf/TwitterLanguageFilterApp.java) following the structure below:

```bash
spark-submit --class edu.upf.TwitterLanguageFilterApp --master local[2] <.jar file path> <language> <output folder> <input file/folder>
```

> [!NOTE]
> The output folder specified must not exists when running the code or else it won't write results there (doesn't overwrite).

An example of call we used could be:

```bash
spark-submit --class edu.upf.TwitterLanguageFilterApp --master local[2]  target/spark-twitter-filter-1.0-SNAPSHOT.jar es ./es /home/alex/SDGE-Labs-2024/lab1/twitter-eurovision-2018/twitter-data-from-2018-eurovision-final/Eurovision3.json /home/alex/SDGE-Labs-2024/lab1/twitter-eurovision-2018/twitter-data-from-2018-eurovision-final/Eurovision4.json /home/alex/SDGE-Labs-2024/lab1/twitter-eurovision-2018/twitter-data-from-2018-eurovision-final/Eurovision5.json /home/alex/SDGE-Labs-2024/lab1/twitter-eurovision-2018/twitter-data-from-2018-eurovision-final/Eurovision6.json /home/alex/SDGE-Labs-2024/lab1/twitter-eurovision-2018/twitter-data-from-2018-eurovision-final/Eurovision7.json /home/alex/SDGE-Labs-2024/lab1/twitter-eurovision-2018/twitter-data-from-2018-eurovision-final/Eurovision8.json /home/alex/SDGE-Labs-2024/lab1/twitter-eurovision-2018/twitter-data-from-2018-eurovision-final/Eurovision9.json /home/alex/SDGE-Labs-2024/lab1/twitter-eurovision-2018/twitter-data-from-2018-eurovision-final/Eurovision10.json
```

### Local benchmarking

<b>Time measurement results are in `ms` (milliseconds).</b>

> [!NOTE]
> In this results we will only show the final processing time (of all files together) and we will skip the output generated by Spark itself.

Results for `es` language:

```
Total number of tweets with language es: 509435
Total time taken: 176286ms
```

Results for `hu` language:

```
Total number of tweets with language hu: 1057
Total time taken: 173935ms
```

Results for `pt` language:

```
Total number of tweets with language pt: 37623
Total time taken: 157478ms
```

## Spark-based TwitterFilter application on EMR

Here we show the results obtained when running the [TwitterLanguageFilterApp](./src/main/java/edu/upf/TwitterLanguageFilterApp.java) on the EMR cluster.

### Cluster execution

### EMR benchmarking

## Most popular Bi-Grams in a given language

### Bi-Grams approach

### Bi-Grams results

## Most retweeted tweets for most retweeted users

### Approach

### Results