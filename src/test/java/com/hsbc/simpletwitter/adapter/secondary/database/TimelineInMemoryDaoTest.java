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
public class TimelineInMemoryDaoTest {

    private static final String USER_UUID = UUID.randomUUID().toString();
    private static final Tweet TWEET = Tweet.builder()
            .userUuid(UUID.randomUUID().toString())
            .uuid(UUID.randomUUID().toString())
            .text("Tweet 1")
            .timestamp(Instant.now())
            .build();

    @Mock
    private Comparator<Tweet> comparator;

    @Mock
    private Map<String, ConcurrentSkipListSet<Tweet>> timelineUser;

    @InjectMocks
    private TimelineInMemoryDao timelineInMemoryDao;


    @Test
    public void addPostToTimeline() {
        //Given
        // When
        timelineInMemoryDao.addPostToTimeline(USER_UUID, TWEET);

        // Then
        Mockito.verify(timelineUser, Mockito.times(1))
                .compute(Mockito.eq(USER_UUID), Mockito.any());
    }

    @Test
    public void retrieveTimelineByUser() {
        //Given
        final ConcurrentSkipListSet<Tweet> expected = new ConcurrentSkipListSet<>(comparator);
        expected.addAll(Collections.singleton(TWEET));

        // When
        Mockito.when(timelineUser.get(USER_UUID))
                .thenReturn(expected);
        Optional<Set<Tweet>> result = timelineInMemoryDao.retrieveTimelineByUser(USER_UUID);

        // Then
        assertThat(result.get()).containsExactly(TWEET);
    }
}