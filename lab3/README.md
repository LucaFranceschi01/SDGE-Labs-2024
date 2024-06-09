# Lab 3
<font color="blue"><b>Authors: Luca Franceschi, Candela Álvarez, Alejandro González</b></font>

> [!TIP]
> We encourage reading this report in the Preview format instead of the Raw format for better visualization.

This report is divided into 3 sections:

- [Main remarks](#main-remarks)
- [Delivery](#delivery)
- [Usage of the programs](#usage-of-the-programs)
- [Our outputs](#our-outputs)

## Main remarks

supress warnings y tsv
All files that use deprecated methods are accompained by their corresponding ```bash@SuppressWarnings("deprecation")```

## Delivery

```bash
├── src
│   └── main
│       ├── java
│       │   ├── com
│       │   │   └── github
│       │   │       └── tukaaa
│       │   │           ├── MastodonDStream.java
│       │   │           ├── MastodonDStreamReceiver.java
│       │   │           ├── adapter
│       │   │           │   └── SimplifiedTweetAdapter.java
│       │   │           ├── config
│       │   │           │   └── AppConfig.java
│       │   │           └── model
│       │   │               └── SimplifiedTweetWithHashtags.java
│       │   └── edu
│       │       └── upf
│       │           ├── MastodonHashtags.java
│       │           ├── MastodonStateless.java
│       │           ├── MastodonStreamingExample.java
│       │           ├── MastodonWindowExample.java
│       │           ├── MastodonWindows.java
│       │           ├── MastodonWithState.java
│       │           ├── model
│       │           │   ├── HashTag.java
│       │           │   ├── HashTagCount.java
│       │           │   └── HashTagCountComparator.java
│       │           ├── storage
│       │           │   ├── DynamoHashTagRepository.java
│       │           │   └── IHashtagRepository.java
│       │           └── util
│       │               └── LanguageMapUtils.java
│       └── resources
│           ├── log4j.properties
│           ├── map.tsv
│           └── map_reduced.tsv
├── target
│   └── lab3-mastodon-1.0-SNAPSHOT.jar
├── pom.xml
├── app.conf
├── log4j.properties
└── README.md

16 directories, 25 files
```

## Usage of the programs

### MastodonStateless execution

```bash
spark-submit --conf spark.driver.extraJavaOptions=-Dlog4j.configuration=file:log4j.properties --class edu.upf.MastodonStateless target/lab3-mastodon-1.0-SNAPSHOT.jar src/main/resources/map_reduced.tsv
```

### MastodonWindows

```bash
spark-submit --conf spark.driver.extraJavaOptions=-Dlog4j.configuration=file:log4j.properties --class edu.upf.MastodonWindows target/lab3-mastodon-1.0-SNAPSHOT.jar src/main/resources/map_reduced.tsv
```

## Our outputs

### MastodonStateless output
```bash
-------------------------------------------
Time: 1710428240000 ms
-------------------------------------------
(English,35)
(Japanese,6)
(German,3)
(French,3)
(Russian,2)
(Portuguese,2)
(Spanish; Castilian,2)
(Korean,1)
(Catalan; Valencian,1)

-------------------------------------------
Time: 1710428260000 ms
-------------------------------------------
(English,244)
(Japanese,53)
(German,29)
(Spanish; Castilian,15)
(French,13)
(Portuguese,7)
(Italian,6)
(Arabic,3)
(Swedish,3)
(Korean,3)
...
```

### MastodonWindows output
```bash
-------------------------------------------
Time: 1710428160000 ms
-------------------------------------------
(283,English)
(68,Japanese)
(40,German)
(17,Spanish; Castilian)
(9,Portuguese)
(9,Dutch; Flemish)
(9,Chinese)
(7,French)
(6,Swedish)
(5,Italian)
(4,Korean)
(2,Arabic)
(2,Finnish)
(2,Romanian; Moldavian; Moldovan)
(2,Romanian; Moldavian; Moldovan)
...

-------------------------------------------
Time: 1710428160000 ms
-------------------------------------------
(283,English)
(68,Japanese)
(40,German)
(17,Spanish; Castilian)
(9,Portuguese)
(9,Dutch; Flemish)
(9,Chinese)
(7,French)
(6,Swedish)
(5,Italian)
(4,Korean)
(2,Arabic)
(2,Finnish)
(2,Romanian; Moldavian; Moldovan)
(2,Romanian; Moldavian; Moldovan)
...
```
In this example we can see that if the language is duplicated in the map_tsv (as happens with Romanian; Moldavian; Moldovan) it shows duplicated in the output.