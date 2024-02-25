package edu.upf.model;
import java.util.Optional;


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
    // IMPLEMENT ME


    }



    /**
    * Returns a {@link ExtendedSimplifiedTweet} from a JSON String.
    * If parsing fails, for any reason, return an {@link Optional#empty()}
    *
    * @param jsonStr
    * @return an {@link Optional} of a {@link ExtendedSimplifiedTweet}
    */
    public static Optional<ExtendedSimplifiedTweet> fromJson(String jsonStr) {
    // IMPLEMENT ME

    
    
    }
}
