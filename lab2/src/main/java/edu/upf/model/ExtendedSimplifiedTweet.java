package edu.upf.model;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import com.google.gson.*;


public class ExtendedSimplifiedTweet implements Serializable {
    private final long tweetId; // the id of the tweet (’id’)
    private final String text; // the content of the tweet (’text’)
    private final long userId; // the user id (’user->id’)
    private final String userName; // the user name (’user’->’name’)
    private final long followersCount; // the number of followers (’user’->’followers_count’)
    private final String language; // the language of a tweet (’lang’)
    private final boolean isRetweeted; // is it a retweet? (the object ’retweeted_status’ exists?)
    private final Long retweetedUserId; // [if retweeted] (’retweeted_status’->’user’->’id’)
    private final Long retweetedTweetId; // [if retweeted] (’retweeted_status’->’id’)
    private final long timestampMs; // seconds from epoch (’timestamp_ms’)
    
    public ExtendedSimplifiedTweet(long tweetId, String text, long userId, String userName,
                                long followersCount, String language, boolean isRetweeted,
                                Long retweetedUserId, Long retweetedTweetId, long timestampMs) {
        //IMPLEMENT ME
        this.tweetId = tweetId;
        this.text = text;
        this.userId = userId;
        this.userName = userName;
        this.followersCount = followersCount;
        this.language = language;
        this.isRetweeted = isRetweeted;
        this.retweetedUserId = retweetedUserId;
        this.retweetedTweetId = retweetedTweetId;
        this.timestampMs = timestampMs;
    }



    /**
    * Returns a {@link ExtendedSimplifiedTweet} from a JSON String.
    * If parsing fails, for any reason, return an {@link Optional#empty()}
    *
    * @param jsonStr
    * @return an {@link Optional} of a {@link ExtendedSimplifiedTweet}
    */
    public static Optional<ExtendedSimplifiedTweet> fromJson(String jsonStr) {
        //IMPLEMENT ME
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();

            Optional<Long> tweetId = Optional.ofNullable(jsonObject.get("id")).map(JsonElement::getAsLong);
            Optional<String> text = Optional.ofNullable(jsonObject.get("text")).map(JsonElement::getAsString);
            Optional<Long> userId = Optional.ofNullable(jsonObject.getAsJsonObject("user").get("id")).map(JsonElement::getAsLong);
            Optional<String> userName = Optional.ofNullable(jsonObject.getAsJsonObject("user").get("name")).map(JsonElement::getAsString);
            Optional<Long> followersCount = Optional.ofNullable(jsonObject.getAsJsonObject("user").get("followers_count")).map(JsonElement::getAsLong);
            Optional<String> language = Optional.ofNullable(jsonObject.get("lang")).map(JsonElement::getAsString);
            Optional<Boolean> isRetweeted = Optional.ofNullable(jsonObject.has("retweeted_status"));
            Optional<Long> retweetedUserId = Optional.ofNullable(jsonObject.getAsJsonObject("retweeted_status").getAsJsonObject("user").get("id")).map(JsonElement::getAsLong);
            Optional<Long> retweetedTweetId = Optional.ofNullable(jsonObject.getAsJsonObject("retweeted_status").get("id")).map(JsonElement::getAsLong);
            Optional<Long> timestampMs = Optional.ofNullable(jsonObject.get("timestamp_ms")).map(JsonElement::getAsLong);


            ExtendedSimplifiedTweet tweet = new ExtendedSimplifiedTweet(tweetId.get(), text.get(), userId.get(), userName.get(), followersCount.get(), language.get(), 
                                                                        isRetweeted.get(), retweetedUserId.get(), retweetedTweetId.get(), timestampMs.get());
            Optional<ExtendedSimplifiedTweet> opt_tweet = Optional.ofNullable(tweet);

            return opt_tweet;   
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

    public Boolean getIsRetweeted() {
        return isRetweeted;
    }

    public Long getRetweetedUserId() {
        return retweetedUserId;
    }

    public Long getRetweetedId() {
        return retweetedTweetId;
    }

    public Long getFollowersCount() {
        return followersCount;
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
