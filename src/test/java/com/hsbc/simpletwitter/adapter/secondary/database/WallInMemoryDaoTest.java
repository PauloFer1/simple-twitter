package com.hsbc.simpletwitter.adapter.secondary.database;

import com.hsbc.simpletwitter.core.model.Tweet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class WallInMemoryDaoTest {

    private static final String USER_UUID = UUID.randomUUID().toString();
    private static final Tweet TWEET = Tweet.builder()
            .userUuid(USER_UUID)
            .uuid(UUID.randomUUID().toString())
            .text("Tweet 1")
            .timestamp(Instant.now())
            .build();

    @Mock
    private Comparator<Tweet> comparator;

    @Mock
    private Map<String, ConcurrentSkipListSet<Tweet>> userWall;

    @InjectMocks
    private WallInMemoryDao wallInMemmoryDao;

    @Test
    public void addPostToTimeline() {
        //Given
        // When
        wallInMemmoryDao.addItem(TWEET.getUserUuid(), TWEET);

        // Then
        Mockito.verify(userWall, Mockito.times(1))
                .compute(Mockito.eq(TWEET.getUserUuid()), Mockito.any());
    }

    @Test
    public void retrieveTimelineByUser() {
        //Given
        final ConcurrentSkipListSet<Tweet> expected = new ConcurrentSkipListSet<>(comparator);
        expected.addAll(Collections.singleton(TWEET));

        // When
        Mockito.when(userWall.get(USER_UUID))
                .thenReturn(expected);
        Optional<Set<Tweet>> result = wallInMemmoryDao.retrieveWallByUser(USER_UUID);

        // Then
        assertThat(result.get()).containsExactly(TWEET);
    }

    @Test
    public void insertPost() {
        //Given
        // When
        wallInMemmoryDao.insertPost(TWEET);

        // Then
        Mockito.verify(userWall, Mockito.times(1))
                .compute(Mockito.eq(USER_UUID), Mockito.any());
    }

}