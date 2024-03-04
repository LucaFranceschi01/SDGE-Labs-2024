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

- [Most popular bi-grams in a given language](#most-popular-bigrams-in-a-given-language)
    1. [Approach](#bigrams-approach)
    2. [Results](#bigrams-results)

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
spark-submit --class edu.upf.TwitterLanguageFilterApp --master local[2] <.jar file path> <language> <output folder> <input file(s)>
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
> In this benchmarking we analyze the total time spent processing all files together, and we will skip the output generated by Spark itself.

The following results come from executing the program with <b> ALL </b> the `.json` files.

As in the statement it was ambiguous which 3 languages we should filter (in one part it said `es`, `ca` and `en` and in another it said `es`, `hu` and `pt`) we decided to benchmark all 5.

Results for `es` language:

```
Total number of tweets with language es: 509435
Total time taken: 176286ms
```

Results for `ca` language:
```
Total number of tweets with language ca: 4583
Total time taken: 184371ms
```

Results for `en` language:
```
Total number of tweets with language en: 446603
Total time taken: 189688ms
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

To execute on the cluster we have to specify several things.

> [!NOTE]
> Be sure to be running on cluster mode.

The first one is the S3 path where the `.jar` of the application is found. In our case it's `s3://lsds2024.lab2.output.u198741/jars/spark-twitter-filter-1.0-SNAPSHOT.jar`.

Then we have to specify the class. In our case we introduced:

```
--class edu.upf.TwitterLanguageFilterApp
```

Lastly, we have the parameters, that remain the same as executing locally, so:

```
<language> <output folder> <input file(s)>
```

An example of the parameters used could be:

```
es s3://lsds2024.lab2.output.u198741/output/benchmark/es s3://lsds2024.lab2.output.u198741/input/Eurovision3.json s3://lsds2024.lab2.output.u198741/input/Eurovision4.json s3://lsds2024.lab2.output.u198741/input/Eurovision5.json s3://lsds2024.lab2.output.u198741/input/Eurovision6.json s3://lsds2024.lab2.output.u198741/input/Eurovision7.json s3://lsds2024.lab2.output.u198741/input/Eurovision8.json s3://lsds2024.lab2.output.u198741/input/Eurovision9.json s3://lsds2024.lab2.output.u198741/input/Eurovision10.json
```

### EMR benchmarking

You can access the bucket with the results [here](https://s3.console.aws.amazon.com/s3/buckets/lsds2024.lab2.output.u198741).

<b>Time measurement results are in `s` (seconds).</b>

> [!NOTE]
> When running on EMR cluster the `print()` calls in the code do not appear in the output, therefore we have to rely on the time provided at the end of the generated file `controller`.

The following results come from executing the program with <b> ALL </b> the `.json` files.

Again, as in the statement it was ambiguous which 3 languages we should filter (in one part it said `es`, `ca` and `en` and in another it said `es`, `hu` and `pt`) we decided to benchmark all 5.

Time taken for `es` language:

```
2024-03-04T20:47:19.303Z INFO Step succeeded with exitCode 0 and took 144 seconds
```

Time taken for `ca` language:

```
2024-03-04T21:54:52.127Z INFO Step succeeded with exitCode 0 and took 116 seconds
```

Time taken for `en` language:

```
2024-03-04T21:43:24.702Z INFO Step succeeded with exitCode 0 and took 142 seconds
```

Time taken for `hu` language:

```
2024-03-04T20:59:51.601Z INFO Step succeeded with exitCode 0 and took 122 seconds
```

Time taken for `pt` language:

```
2024-03-04T21:06:01.748Z INFO Step succeeded with exitCode 0 and took 120 seconds
```

We can see that the execution time taken in the cluster is lower than the one running locally.

## Most popular Bigrams in a given language

### Bigrams approach

In order to obtain the most popular bigrams, we started in a very similar way to the `TwitterLanguageFilterApp`: we joined all the tweets from all the input files in a single RDD and then passing them to instances of the [ExtendedSimplifiedTweet](./src/main/java/edu/upf/model/ExtendedSimplifiedTweet.java) class. Then we only kept the non-empty ones and with the specified language.

Then, to create the bigrams, we first splitted the sentences into words, and created all bigrams of a sentence. To do this we used 2 functions.

This function removes all starting and trailing whitespaces (trimming) and converts the string to lower case.

```java
private static String normalise(String word) {
    return word.trim().toLowerCase();
}
```

This function divides the given sentence into words, and then creates all possible bigrams <b>normalized</b>, checking that they are actually words and not the empty string. We will use this function in a `map` transformation on the `tweets` RDD.

```java
private static List<Tuple2<String, String>> bigramsFromText(String text) {

    List<Tuple2<String, String>> bigrams = new ArrayList<Tuple2<String, String>>();
    
    List<String> words = Arrays.asList(text.split(" "));

    for (int ind = 0; ind < words.size() - 2; ind++) {
        if (!normalise(words.get(ind)).isEmpty() && !normalise(words.get(ind+1)).isEmpty()) {
            bigrams.add(new Tuple2<String,String>(normalise(words.get(ind)), normalise(words.get(ind+1))));
        }
    }

    return bigrams;
}
```

With these 2 functions we can get all bigrams with the corresponding amount of times they appear in the text of the tweets. The code below shows how:

```java
JavaPairRDD<Tuple2<String, String>, Long> bigrams = filteredTweets
    .map(tweet -> tweet.get().getText())
    .flatMap(text -> bigramsFromText(text).iterator())
    .mapToPair(bigram -> new Tuple2<Tuple2<String, String>, Long>(bigram, 1L))
    .reduceByKey((a, b) -> a + b);
```

Basically, we computed all bigrams from the text of each tweet, created a key-value RDD with the bigram and value 1, and reduced using the `+` operation to obtain number of times bigram appears.

Lastly, we just need to order the values descending. To do that, we need to swap the key and value, sort and swap back. This is because there is no function to order by value, just the `sortByKey` function. The code below performs the descending order:

```java
JavaPairRDD<Tuple2<String, String>, Long> bigrams_descending_frequency = bigrams
    .mapToPair(tuple -> new Tuple2<Long, Tuple2<String, String>>(tuple._2, tuple._1))
    .sortByKey(false)
    .mapToPair(tuples -> new Tuple2<Tuple2<String, String>, Long>(tuples._2, tuples._1));
```

### Bigrams results

The following results come from executing the program with <b> ALL </b> the `.json` files.

The top 10 most popular bigrams obtained and their number of times appeared were:

```
((en,el),21442)
((de,la),19658)
((que,no),15182)
((#eurovision,#finaleurovision),14573)
((en,#eurovision),13929)
((en,la),13115)
((el,año),12588)
((lo,que),12423)
((a,la),11789)
((que,el),11497)
```

## Most retweeted tweets for most retweeted users

### Approach

### Results