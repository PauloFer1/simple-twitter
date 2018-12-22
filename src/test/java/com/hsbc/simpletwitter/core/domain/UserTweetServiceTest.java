package com.hsbc.simpletwitter.core.domain;

import com.hsbc.simpletwitter.core.model.Tweet;
import com.hsbc.simpletwitter.port.secondary.TimelineDao;
import com.hsbc.simpletwitter.port.secondary.WallDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class UserTweetServiceTest {

    private static final String USER_UUID = UUID.randomUUID().toString();
    private static final Tweet TWEET = Tweet.builder()
            .text("tweet")
            .timestamp(Instant.now())
            .userUuid(USER_UUID)
            .uuid(UUID.randomUUID().toString())
            .build();

    @Mock
    private TimelineDao timelineDao;

    @Mock
    private WallDao wallDao;

    @InjectMocks
    private UserTweetService userTweetService;

    @Test
    public void getUserTimelineReturnsEmptySet() {
        // Given
        // When
        Mockito.when(timelineDao.retrieveTimelineByUser(USER_UUID))
                .thenReturn(Optional.empty());
        Set<Tweet> result = userTweetService.getUserTimeline(USER_UUID);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    public void getUserTimeline() {
        // Given
        // When
        Mockito.when(timelineDao.retrieveTimelineByUser(USER_UUID))
                .thenReturn(Optional.of(new HashSet<>(Collections.singleton(TWEET))));
        Set<Tweet> result = userTweetService.getUserTimeline(USER_UUID);

        // Then
        assertThat(result).containsExactly(TWEET);
    }

    @Test
    public void getUserWallReturnsEmptySet() {
        // Given
        // When
        Mockito.when(wallDao.retrieveWallByUser(USER_UUID))
                .thenReturn(Optional.empty());
        Set<Tweet> result = userTweetService.getUserWall(USER_UUID);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    public void getUserWall() {
        // Given
        // When
        Mockito.when(wallDao.retrieveWallByUser(USER_UUID))
                .thenReturn(Optional.of(new HashSet<>(Collections.singleton(TWEET))));
        Set<Tweet> result = userTweetService.getUserWall(USER_UUID);

        // Then
        assertThat(result).containsExactly(TWEET);
    }
}