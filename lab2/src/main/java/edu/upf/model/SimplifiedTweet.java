package edu.upf.model;

import java.util.Optional;

import com.google.gson.*;

public class SimplifiedTweet {

    private final long tweetId;			  // the id of the tweet ('id')
    private final String text;  		      // the content of the tweet ('text')
    private final long userId;			  // the user id ('user->id')
    private final String userName;		  // the user name ('user'->'name')
    private final String language;          // the language of a tweet ('lang')
    private final long timestampMs;		  // seconduserIds from epoch ('timestamp_ms')

    public SimplifiedTweet(long tweetId, String text, long userId, String userName, String language, long timestampMs) {
        this.tweetId = tweetId;
        this.text = text;
        this.userId = userId;
        this.userName = userName;
        this.language = language;
        this.timestampMs = timestampMs;
    }

    /**
     * Returns a {@link SimplifiedTweet} from a JSON String.
     * If parsing fails, for any reason, return an {@link Optional#empty()}
     *
     * @param jsonStr
     * @return an {@link Optional} of a {@link SimplifiedTweet}
     */
    public static Optional<SimplifiedTweet> fromJson(String jsonStr) {

        try {
            JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();

            Optional<Long> tweetId = Optional.ofNullable(jsonObject.get("id")).map(JsonElement::getAsLong);
            Optional<String> text = Optional.ofNullable(jsonObject.get("text")).map(JsonElement::getAsString);
            Optional<Long> userId = Optional.ofNullable(jsonObject.getAsJsonObject("user").get("id")).map(JsonElement::getAsLong);
            Optional<String> userName = Optional.ofNullable(jsonObject.getAsJsonObject("user").get("name")).map(JsonElement::getAsString);
            Optional<String> language = Optional.ofNullable(jsonObject.get("lang")).map(JsonElement::getAsString);
            Optional<Long> timestampMs = Optional.ofNullable(jsonObject.get("timestamp_ms")).map(JsonElement::getAsLong);

            SimplifiedTweet tweet = new SimplifiedTweet(tweetId.get(), text.get(), userId.get(), userName.get(), language.get(), timestampMs.get());
            Optional<SimplifiedTweet> opt_tweet = Optional.ofNullable(tweet);
            
            // Optional<SimplifiedTweet> opt_tweet =  tweetId.flatMap(tid ->
            //     text.flatMap(txt -> 
            //         userId.flatMap(uid ->
            //             userName.flatMap(uName ->
            //                 language.flatMap(lang ->
            //                     timestampMs.map(ts -> 
            //                         new SimplifiedTweet(tid, txt, uid, uName, lang, ts)
            //                     )
            //                 )
            //             )
            //         )
            //     )
            // );

            System.out.println(opt_tweet.toString());
            return opt_tweet;

        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

    public String getLanguage() {
        return language;
    }

    public long getTimestampMs() {
        return timestampMs;
    }

    public String getText() {
        return text;
    }

    public long getTweetId() {
        return tweetId;
    }

    public long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
