package com.hsbc.simpletwitter.core.domain.validation;

import com.hsbc.simpletwitter.core.exception.MaxTweetCharactersException;

import javax.inject.Named;

@Named
public class TweetValidation {

    private static final int TWEET_MAX_LENGTH = 140;

    public String validateTweet(final String userUuid, final String tweet) {
        if (tweet.length() > TWEET_MAX_LENGTH) {
            throw new MaxTweetCharactersException(userUuid, TWEET_MAX_LENGTH);
        }
        return tweet;
    }
}
