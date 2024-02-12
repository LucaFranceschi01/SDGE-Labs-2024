# Lab 1
<font color="blue"><b>Authors: Luca Franceschi, Candela Álvarez, Alejandro González</b></font>

> [!TIP]
> We encourage reading this report in the preview format instead
> of the raw format for better visualization.

This report is divided into 2 sections:
- [Source code changes and justification](#source-code-changes-and-justification)
  1. [Return type of function in interface LanguageFilter](#return-type-of-function-in-interface-languagefilter)
  2. [Removal of deprecated JsonParser instance](#removal-of-deprecated-jsonparser-instance)
  3. [Change of `upf` profile to `default` profile](#change-of-upf-profile-to-default-profile)
- [Benchmarking](#benchmarking)

## Source code changes and justification

### Return type of function in interface LanguageFilter
***

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
***

We removed the following variable in the class [SimplifiedTweet](./src/main/java/edu/upf/parser/SimplifiedTweet.java) as it was deprecated.

```java
private static JsonParser parser = new JsonParser();
```

Then we just used the static method provided by JsonParser.

```java
JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
```

### Change of `upf` profile to `default` profile
***

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