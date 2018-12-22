package com.hsbc.simpletwitter.core.exception;

public class MaxTweetCharactersException extends RuntimeException {

    private static final String TEMPLATE = "Tweet Exceeded %d characters for USER: %s";

    public MaxTweetCharactersException(final String userUuid, final int maxLength) {
        super(String.format(TEMPLATE, maxLength, userUuid));
    }
}
