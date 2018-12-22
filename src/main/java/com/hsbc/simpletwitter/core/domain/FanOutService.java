package com.hsbc.simpletwitter.core.domain;

import com.hsbc.simpletwitter.core.domain.validation.TweetValidation;
import com.hsbc.simpletwitter.core.exception.UserCannotFollowHimselfException;
import com.hsbc.simpletwitter.core.model.Tweet;
import com.hsbc.simpletwitter.port.secondary.FollowersDao;
import com.hsbc.simpletwitter.port.secondary.TimelineDao;
import com.hsbc.simpletwitter.port.secondary.WallDao;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import java.time.Instant;
import java.util.UUID;

@Named
@RequiredArgsConstructor
public class FanOutService {

    private final FollowersDao followersDao;
    private final TimelineDao timelineDao;
    private final WallDao wallDao;
    private final TweetValidation tweetValidation;

    public Tweet postTweet(final String userUuid, final String text) {
        final Tweet tweetToInsert = Tweet.builder()
                .timestamp(Instant.now())
                .text(tweetValidation.validateTweet(userUuid, text))
                .userUuid(userUuid)
                .uuid(UUID.randomUUID().toString())
                .build();
        wallDao.insertPost(tweetToInsert);
        fanToFollowers(tweetToInsert);
        return tweetToInsert;
    }

    private void fanToFollowers(final Tweet tweet) {
        followersDao.getFollowers(tweet.getUserUuid()).ifPresent(
                set -> set.forEach(follower -> timelineDao.addPostToTimeline(follower, tweet))
        );
    }

    public void followUser(final String userUuid, final String followee) {
        if (userUuid.equals(followee)) {
            throw new UserCannotFollowHimselfException(userUuid);
        }
        followersDao.addFollower(followee, userUuid);
    }
}
