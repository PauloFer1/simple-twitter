package com.hsbc.simpletwitter.core.domain;

import com.hsbc.simpletwitter.core.domain.validation.TweetValidation;
import com.hsbc.simpletwitter.core.exception.MaxTweetCharactersException;
import com.hsbc.simpletwitter.core.exception.UserCannotFollowHimselfException;
import com.hsbc.simpletwitter.core.model.Tweet;
import com.hsbc.simpletwitter.port.secondary.FollowersDao;
import com.hsbc.simpletwitter.port.secondary.TimelineDao;
import com.hsbc.simpletwitter.port.secondary.WallDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FanOutServiceTest {

    private static final int TWEET_MAX_LENGTH = 140;

    private static final String USER_UUID = UUID.randomUUID().toString();

    @Mock
    private FollowersDao followersDao;

    @Mock
    private TimelineDao timelineDao;

    @Mock
    private WallDao wallDao;

    @Mock
    private TweetValidation tweetValidation;

    @InjectMocks
    private FanOutService fanOutService;

    @Captor
    private ArgumentCaptor<Tweet> tweetArgumentCaptor;

    @Test
    public void postTweetWithInvalidLengthThrowsException() {
        // Given
        final String tweet = "Very long tweet";

        // When
        Mockito.when(tweetValidation.validateTweet(USER_UUID, tweet))
                .thenThrow(new MaxTweetCharactersException(USER_UUID, TWEET_MAX_LENGTH));

        // Then
        assertThatExceptionOfType(MaxTweetCharactersException.class)
                .isThrownBy(() -> fanOutService.postTweet(USER_UUID, tweet))
                .withMessageContaining("Tweet Exceeded " + TWEET_MAX_LENGTH + " characters for USER: " + USER_UUID);
    }

    @Test
    public void postTweetInsertOnWallDao() {
        // Given
        final String tweetText = "tweet";
        final Tweet tweet = Tweet.builder()
                .timestamp(Instant.now())
                .text(tweetText)
                .userUuid(USER_UUID)
                .uuid(UUID.randomUUID().toString())
                .build();

        // When
        Mockito.when(tweetValidation.validateTweet(USER_UUID, tweetText))
                .thenReturn(tweetText);
        fanOutService.postTweet(USER_UUID, tweetText);

        // Then
        Mockito.verify(wallDao).insertPost(tweetArgumentCaptor.capture());
        assertThat(tweetArgumentCaptor.getValue())
                .isEqualToComparingOnlyGivenFields(tweet, "text", "userUuid");
        assertThat(tweetArgumentCaptor.getValue().getUuid()).isNotEmpty();
        assertThat(tweetArgumentCaptor.getValue().getTimestamp())
                .isBetween(Instant.now().minusSeconds(5L), Instant.now().plusSeconds(1L));
    }

    @Test
    public void postTweetInsertOnTimelineDao() {
        // Given
        final String tweetText = "tweet";
        final Tweet tweet = Tweet.builder()
                .timestamp(Instant.now())
                .text(tweetText)
                .userUuid(USER_UUID)
                .uuid(UUID.randomUUID().toString())
                .build();
        final String followerUuid = UUID.randomUUID().toString();

        // When
        Mockito.when(tweetValidation.validateTweet(USER_UUID, tweetText))
                .thenReturn(tweetText);
        Mockito.when(followersDao.getFollowers(USER_UUID))
                .thenReturn(Optional.of(new HashSet<>(Collections.singleton(followerUuid))));
        fanOutService.postTweet(USER_UUID, tweetText);

        // Then
        Mockito.verify(timelineDao).addPostToTimeline(Mockito.eq(followerUuid), tweetArgumentCaptor.capture());
        assertThat(tweetArgumentCaptor.getValue())
                .isEqualToComparingOnlyGivenFields(tweet, "text", "userUuid");
        assertThat(tweetArgumentCaptor.getValue().getUuid()).isNotEmpty();
        assertThat(tweetArgumentCaptor.getValue().getTimestamp())
                .isBetween(Instant.now().minusSeconds(5L), Instant.now().plusSeconds(1L));
    }

    @Test
    public void postTweetDoNotInsertOnTimelineDao() {
        // Given
        final String tweetText = "tweet";

        // When
        Mockito.when(tweetValidation.validateTweet(USER_UUID, tweetText))
                .thenReturn(tweetText);
        Mockito.when(followersDao.getFollowers(USER_UUID))
                .thenReturn(Optional.empty());
        fanOutService.postTweet(USER_UUID, tweetText);

        // Then
        Mockito.verifyZeroInteractions(timelineDao);
    }

    @Test
    public void followUserSameIdsThrowException() {
        // Given
        // When
        // Then
        Mockito.verifyZeroInteractions(followersDao);
        assertThatExceptionOfType(UserCannotFollowHimselfException.class)
                .isThrownBy(() -> fanOutService.followUser(USER_UUID, USER_UUID))
                .withMessageContaining("User cannot follow himself: " + USER_UUID);
    }

    @Test
    public void followUser() {
        // Given
        final String followeeUuid = UUID.randomUUID().toString();

        // When
        fanOutService.followUser(USER_UUID,followeeUuid);

        // Then
        Mockito.verify(followersDao, Mockito.times(1))
                .addFollower(followeeUuid, USER_UUID);
    }

}