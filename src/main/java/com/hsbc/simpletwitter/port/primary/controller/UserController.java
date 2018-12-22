package com.hsbc.simpletwitter.port.primary.controller;

import com.hsbc.simpletwitter.port.primary.controller.model.FollowerDto;
import com.hsbc.simpletwitter.port.primary.controller.model.TweetDto;

public interface UserController<T> {

    T postTweet(final TweetDto post);
    T viewWall(final String userUuid);
    T followUser(final FollowerDto follower);
    T viewTimeline(final String userUuid);
}
