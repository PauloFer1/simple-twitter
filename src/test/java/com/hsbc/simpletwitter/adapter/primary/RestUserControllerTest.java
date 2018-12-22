package com.hsbc.simpletwitter.adapter.primary;

import com.hsbc.simpletwitter.adapter.primary.controller.RestUserController;
import com.hsbc.simpletwitter.core.domain.FanOutService;
import com.hsbc.simpletwitter.core.domain.UserTweetService;
import com.hsbc.simpletwitter.core.model.Timeline;
import com.hsbc.simpletwitter.core.model.Tweet;
import com.hsbc.simpletwitter.core.model.Wall;
import com.hsbc.simpletwitter.port.primary.controller.model.FollowerDto;
import com.hsbc.simpletwitter.port.primary.controller.model.TweetDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class RestUserControllerTest {

    private static final String USER_UUID = UUID.randomUUID().toString();

    @Mock
    private UserTweetService userTweetService;

    @Mock
    private FanOutService fanOutService;

    @InjectMocks
    private RestUserController restUserController;


    @Test
    public void postTweet() {
        // Given
        final TweetDto tweetDto = TweetDto.builder()
                .text("tweet")
                .userUuid(USER_UUID)
                .build();

        final Tweet tweet = Tweet.builder()
                .text(tweetDto.getText())
                .userUuid(USER_UUID)
                .timestamp(Instant.now())
                .uuid(UUID.randomUUID().toString())
                .build();

        // When
        Mockito.when(fanOutService.postTweet(tweetDto.getUserUuid(), tweetDto.getText()))
                .thenReturn(tweet);
        ResponseEntity responseEntity = restUserController.postTweet(tweetDto);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(tweet);
    }

    @Test
    public void viewWall() {
        // Given
        final Tweet tweet = Tweet.builder()
                .text("Tweet")
                .userUuid(USER_UUID)
                .timestamp(Instant.now())
                .uuid(UUID.randomUUID().toString())
                .build();
        final Set<Tweet> tweets = new HashSet<>(Collections.singleton(tweet));
        // When
        Mockito.when(userTweetService.getUserWall(USER_UUID))
                .thenReturn(tweets);
        ResponseEntity responseEntity = restUserController.viewWall(USER_UUID);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isEqualTo(Wall.builder().userUuid(USER_UUID).tweets(tweets).build());
    }

    @Test
    public void followUser() {
        // Given
        final String followerUuid = UUID.randomUUID().toString();
        final FollowerDto followerDto = FollowerDto.builder()
                .userUuid(USER_UUID)
                .userUuidToFollow(followerUuid)
                .build();

        // When
        ResponseEntity responseEntity = restUserController.followUser(followerDto);

        // Then
        Mockito.verify(fanOutService, Mockito.times(1))
                .followUser(USER_UUID, followerUuid);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(followerDto);
    }

    @Test
    public void viewTimeline() {
        // Given
        final Tweet tweet = Tweet.builder()
                .text("Tweet")
                .userUuid(USER_UUID)
                .timestamp(Instant.now())
                .uuid(UUID.randomUUID().toString())
                .build();
        final Set<Tweet> tweets = new HashSet<>(Collections.singleton(tweet));

        // When
        Mockito.when(userTweetService.getUserTimeline(USER_UUID))
                .thenReturn(tweets);
        ResponseEntity responseEntity = restUserController.viewTimeline(USER_UUID);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isEqualTo(Timeline.builder().tweets(tweets).userUuid(USER_UUID).build());
    }

}