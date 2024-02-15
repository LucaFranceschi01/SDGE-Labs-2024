# Lab 1
<font color="blue"><b>Authors: Luca Franceschi, Candela Álvarez, Alejandro González</b></font>

> [!TIP]
> We encourage reading this report in the Preview format instead
> of the Raw format for better visualization.

This report is divided into 2 sections:
- [Source code changes and justification](#source-code-changes-and-justification)
  1. [Return type of function in interface LanguageFilter](#return-type-of-function-in-interface-languagefilter)
  2. [Removal of deprecated JsonParser instance](#removal-of-deprecated-jsonparser-instance)
  3. [Change of `upf` profile to `default` profile](#change-of-upf-profile-to-default-profile)
- [Benchmarking](#benchmarking)
  1. [Execution of the program](#execution-of-the-program)
  2. [S3 bucket: output and structure](#s3-bucket-output-and-structure)
  3. [Benchmarking outputs](#benchmarking-outputs)
  4. [Execution time insights](#execution-time-insights)

## Source code changes and justification

### Return type of function in interface LanguageFilter

We changed the given definition of the function below in [LanguageFilter](./src/main/java/edu/upf/filter/LanguageFilter.java)
which is implemented by [FileLanguageFilter](./src/main/java/edu/upf/filter/FileLanguageFilter.java).

```java
void filterLanguage(String language) throws Exception;
```

To this definition:

```java
Integer filterLanguage(String language) throws Exception;
```

This change was made to compute the tweet count while filtering the file.

### Removal of deprecated JsonParser instance

We removed the following variable in the class [SimplifiedTweet](./src/main/java/edu/upf/parser/SimplifiedTweet.java) as it was deprecated.

```java
private static JsonParser parser = new JsonParser();
```

Then we just used the static method provided by JsonParser.

```java
JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
```

### Change of `upf` profile to `default` profile

We changed the call to constructor of [S3Uploader](./src/main/java/edu/upf/uploader/S3Uploader.java) in the 
[TwitterFilter](./src/main/java/edu/upf/TwitterFilter.java) file.

```java
final S3Uploader uploader = new S3Uploader(bucket, "prefix", "upf");
```

We replaced it by the call below.

```java
final S3Uploader uploader = new S3Uploader(bucket, language, "default");
```

This change was made to generate the correct `prefix` when uploading to S3, and to check for `default` profile instead of `upf` profile. 

> [!WARNING]
> If you have the profile defined as `upf`, you can change the
> call to the following one and will work as well.

```java
final S3Uploader uploader = new S3Uploader(bucket, language, "upf");
```

## Benchmarking

The benchmarking of the program has been done using all the files extracted from the `twitter-eurovision-2018.tar` at the same time.

Below are all the `.json` files obtained.

```bash
└── twitter-eurovision-2018
    ├── Eurovision3.json
    ├── Eurovision4.json
    ├── Eurovision5.json
    ├── Eurovision6.json
    ├── Eurovision7.json
    ├── Eurovision8.json
    ├── Eurovision9.json
    └── Eurovision10.json

1 directory, 8 files
```

<b>Time mesurement results are in `ms` (milliseconds)</b>

### Execution of the program

The call to the program follows the form given below:

```bash
java -cp jarfile edu.upf.TwitterFilter arg1 arg2 ... argN
```

Where `arg1 ... argN` are the corresponding arguments:

```
<language> <path to local output> <S3 destination bucket> <.json files>
```

Here is an example call to the program we used.

```bash
java -cp target/lab1-1.0-SNAPSHOT.jar edu.upf.TwitterFilter es /home/lukovsky/Documents/SDGE/tmp/out.txt lsds2024.lab1.output.u199149 twitter-eurovision-2018/Eurovision3.json twitter-eurovision-2018/Eurovision4.json twitter-eurovision-2018/Eurovision5.json twitter-eurovision-2018/Eurovision6.json twitter-eurovision-2018/Eurovision7.json twitter-eurovision-2018/Eurovision8.json twitter-eurovision-2018/Eurovision9.json twitter-eurovision-2018/Eurovision10.json
```

### S3 bucket: output and structure

The output is in the S3 bucket called `lsds2024.lab1.output.u199149`.

You can access it [here](https://s3.console.aws.amazon.com/s3/buckets/lsds2024.lab1.output.u199149).

Its contents are the following:

```bash
2024-02-15 10:59:34     269348 ca/out.json
2024-02-15 10:59:18   28273325 en/out.json
2024-02-15 10:58:37   42360049 es/out.json
```

### Benchmarking outputs

<b>Time measurement results are in `ms` (milliseconds)</b>

Results obtained for `es` language:

```
=============================== PROCESSING THE FILES ================================

Processing: twitter-eurovision-2018/Eurovision3.json
Filtered 23848 tweets in 7877ms

Processing: twitter-eurovision-2018/Eurovision4.json
Filtered 78433 tweets in 12220ms

Processing: twitter-eurovision-2018/Eurovision5.json
Filtered 45800 tweets in 6444ms

Processing: twitter-eurovision-2018/Eurovision6.json
Filtered 71677 tweets in 10050ms

Processing: twitter-eurovision-2018/Eurovision7.json
Filtered 54969 tweets in 9792ms

Processing: twitter-eurovision-2018/Eurovision8.json
Filtered 38805 tweets in 10461ms

Processing: twitter-eurovision-2018/Eurovision9.json
Filtered 26244 tweets in 7301ms

Processing: twitter-eurovision-2018/Eurovision10.json
Filtered 169659 tweets in 45218ms


===================================== RESULTS ======================================

Total number of tweets with language es: 509435
Total time taken: 109365ms
```

Results obtained for `en` language:

```
=============================== PROCESSING THE FILES ================================

Processing: twitter-eurovision-2018/Eurovision3.json
Filtered 24346 tweets in 8481ms

Processing: twitter-eurovision-2018/Eurovision4.json
Filtered 96430 tweets in 12247ms

Processing: twitter-eurovision-2018/Eurovision5.json
Filtered 50545 tweets in 6823ms

Processing: twitter-eurovision-2018/Eurovision6.json
Filtered 66596 tweets in 10607ms

Processing: twitter-eurovision-2018/Eurovision7.json
Filtered 39794 tweets in 10004ms

Processing: twitter-eurovision-2018/Eurovision8.json
Filtered 35569 tweets in 10459ms

Processing: twitter-eurovision-2018/Eurovision9.json
Filtered 18048 tweets in 6823ms

Processing: twitter-eurovision-2018/Eurovision10.json
Filtered 115275 tweets in 39571ms


===================================== RESULTS ======================================

Total number of tweets with language en: 446603
Total time taken: 105016ms
```

Results for `ca` language:

```
=============================== PROCESSING THE FILES ================================

Processing: twitter-eurovision-2018/Eurovision3.json
Filtered 242 tweets in 7749ms

Processing: twitter-eurovision-2018/Eurovision4.json
Filtered 983 tweets in 11970ms

Processing: twitter-eurovision-2018/Eurovision5.json
Filtered 581 tweets in 6288ms

Processing: twitter-eurovision-2018/Eurovision6.json
Filtered 717 tweets in 9854ms

Processing: twitter-eurovision-2018/Eurovision7.json
Filtered 398 tweets in 10032ms

Processing: twitter-eurovision-2018/Eurovision8.json
Filtered 404 tweets in 9764ms

Processing: twitter-eurovision-2018/Eurovision9.json
Filtered 193 tweets in 6618ms

Processing: twitter-eurovision-2018/Eurovision10.json
Filtered 1065 tweets in 38782ms


===================================== RESULTS ======================================

Total number of tweets with language ca: 4583
Total time taken: 101058ms
```

### Execution time insights

From the results we can see that the executions for any language take almost the same time (small difference of $\pm$ $4000$ ms, with mean $105146$ ms), even though there is a huge difference in the tweets filtered (e.g. `ca` language has only $4583$ tweets filtered against the $509435$ tweets in `es` language).

These results make sense as each execution has to read all lines of all files, it doesn't matter the language we want to filter. 

We can also extract from this that writing to output file is pretty efficient as there is barely any difference in execution time given the huge difference in tweets written.