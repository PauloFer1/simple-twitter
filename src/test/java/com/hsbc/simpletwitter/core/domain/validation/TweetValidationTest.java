package com.hsbc.simpletwitter.core.domain.validation;

import com.hsbc.simpletwitter.core.exception.MaxTweetCharactersException;
import org.junit.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class TweetValidationTest {

    private static final int TWEET_MAX_LENGTH = 140;

    private TweetValidation tweetValidation = new TweetValidation();

    @Test
    public void validateTweetWithLessThenMaxTweetLength() {
        // Given
        final String tweet = "Small Tweet";

        // When
        String result = tweetValidation.validateTweet("user", tweet);

        // Then
        assertThat(result).isEqualTo(tweet);
    }

    @Test
    public void validateTweetWithMaxTweetLength() {
        // Given
        final StringBuilder tweet = new StringBuilder();
        IntStream.range(0, TWEET_MAX_LENGTH)
                .forEach(i -> tweet.append((char)i));

        // When
        String result = tweetValidation.validateTweet("user", tweet.toString());

        // Then
        assertThat(result).isEqualTo(tweet.toString());
    }

    @Test
    public void validateTweetHigherThenMaxTweetLength() {
        // Given
        final StringBuilder tweet = new StringBuilder();
        IntStream.range(0, TWEET_MAX_LENGTH + 1)
                .forEach(i -> tweet.append((char)i));

        // When
        // Then
        assertThatExceptionOfType(MaxTweetCharactersException.class)
                .isThrownBy(() -> tweetValidation.validateTweet("user", tweet.toString()))
                .withMessageContaining("Tweet Exceeded 140 characters for USER: user");
    }

}