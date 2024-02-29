# Lab 2
<font color="blue"><b>Authors: Luca Franceschi, Candela Álvarez, Alejandro González</b></font>

> [!TIP]
> We encourage reading this report in the Preview format instead of the Raw format for better visualization.

This report is divided into 4 sections:

- [Handout information](#handout-information)

- [Spark-based TwitterFilter application on EMR](#spark-based-twitterfilter-application-on-emr)
    1. [Changes to the previous delivery](#changes-to-the-previous-delivery)
    2. [Benchmarking](#benchmarking-twitterfilter)

- [Most popular bi-grams in a given language](#most-popular-bi-grams-in-a-given-language)
    1. [Approach](#bi-grams-approach)
    2. [Results](#bi-grams-results)

- [Most Retweeted Tweets for Most Retweeted Users](#most-retweeted-tweets-for-most-retweeted-users)
    1. [Approach](#approach)
    2. [Results](#results)

## Handout information

The delivered LSDS-G8-lab2.zip file should be structured as follows:

```bash
LSDS-G8-lab2.zip
├── pom.xml
├── README.md
├── execution.txt
├── src
│   └── main
│       └── java
│           └── edu
│               └── upf
│                   ├── BiGramsApp.java
│                   ├── MostRetweetedApp.java
│                   ├── TwitterLanguageFilterApp.java
│                   ├── filter
│                   │   ├── FileLanguageFilter.java
│                   │   └── LanguageFilter.java
│                   └── model
│                       ├── ExtendedSimplifiedTweet.java
│                       └── SimplifiedTweet.java
└── target
    ├── classes
    │   └── edu
    │       └── upf
    │           ├── BiGramsApp.class
    │           ├── MostRetweetedApp.class
    │           ├── TwitterLanguageFilterApp.class
    │           ├── filter
    │           │   ├── FileLanguageFilter.class
    │           │   └── LanguageFilter.class
    │           └── model
    │               ├── ExtendedSimplifiedTweet.class
    │               └── SimplifiedTweet.class
    ├── generated-sources
    │   └── annotations
    ├── maven-archiver
    │   └── pom.properties
    ├── maven-status
    │   └── maven-compiler-plugin
    │       └── compile
    │           └── default-compile
    │               ├── createdFiles.lst
    │               └── inputFiles.lst
    └── spark-twitter-filter-1.0-SNAPSHOT.jar

21 directories, 21 files
```

> [!NOTE]
> Due to an inconsistency in the handout we did not know where to place BigramsApp.java but we believe it should be placed inside edu.upf directory.

And the s3 bucket is structured as requested in the handout.

## Spark-based TwitterFilter application on EMR

### Changes to the previous delivery

### Benchmarking TwitterFilter
no benchmarking because it does not work

## Most popular Bi-Grams in a given language

### Bi-Grams approach

### Bi-Grams results

## Most retweeted tweets for most retweeted users

### Approach

### Results