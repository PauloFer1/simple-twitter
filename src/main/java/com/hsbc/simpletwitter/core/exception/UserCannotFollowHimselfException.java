package com.hsbc.simpletwitter.core.exception;

public class UserCannotFollowHimselfException extends RuntimeException {

    private static final String TEMPLATE = "User cannot follow himself: %s";

    public UserCannotFollowHimselfException(final String userUuid) {
        super(String.format(TEMPLATE, userUuid));
    }
}
