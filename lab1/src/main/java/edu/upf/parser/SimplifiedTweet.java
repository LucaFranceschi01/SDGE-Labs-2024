package edu.upf.parser;

import java.util.Optional;

import javax.swing.text.html.Option;

import com.google.gson.*;

public class SimplifiedTweet {

  // All classes use the same instance
  private static JsonParser parser = new JsonParser();

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

    // PLACE YOUR CODE HERE!
    
    try {
      JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();

      long tweetId = jsonObject.get("id").getAsLong();
      String text = jsonObject.get("text").getAsString();
      long userId = jsonObject.getAsJsonObject().get("id").getAsLong();
      String userName = jsonObject.getAsJsonObject().get("name").getAsString();
      String language = jsonObject.get("lang").getAsString();
      long timestampMs = jsonObject.get("timestamp_ms").getAsLong();

      SimplifiedTweet tweet = new SimplifiedTweet(tweetId, text, userId, userName, language, timestampMs);
      Optional<SimplifiedTweet> opt_tweet = Optional.ofNullable(tweet); // ??
      return opt_tweet;

    }
    catch (Exception e) {
      return Optional.empty();
    }
  }

  public String getLanguage() {
    return language;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}
