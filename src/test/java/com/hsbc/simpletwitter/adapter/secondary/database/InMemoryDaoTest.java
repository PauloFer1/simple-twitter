package com.hsbc.simpletwitter.adapter.secondary.database;

import com.hsbc.simpletwitter.adapter.secondary.database.comparator.PostComparator;
import com.hsbc.simpletwitter.core.model.Tweet;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryDaoTest {

    private static final String USER_UUID = UUID.randomUUID().toString();
    private static final Tweet TWEET = Tweet.builder()
            .userUuid(USER_UUID)
            .uuid(UUID.randomUUID().toString())
            .text("Tweet 1")
            .timestamp(Instant.now())
            .build();

    private Comparator<Tweet> comparator = new PostComparator();

    private InMemoryDao<String, Tweet> inMemoryDao;

    @Before
    public void SetUp() {
        Map<String, ConcurrentSkipListSet<Tweet>> cache = new ConcurrentHashMap<>();
        inMemoryDao = new InMemoryDao<>(comparator, cache);
    }

    @Test
    public void addItemWithInexistentKey() {
        //Given
        // When
        inMemoryDao.addItem(TWEET.getUserUuid(), TWEET);
        Set<Tweet> result = inMemoryDao.getItem(USER_UUID);

        // Then
        assertThat(result).containsExactly(TWEET);
    }

    @Test
    public void addItemWithExistentKey() {
        //Given
        final Tweet tweet2 = Tweet.builder()
                .userUuid(USER_UUID)
                .uuid(UUID.randomUUID().toString())
                .text("Tweet 2")
                .timestamp(Instant.now().minusSeconds(1000L))
                .build();

        // When
        inMemoryDao.addItem(TWEET.getUserUuid(), TWEET);
        inMemoryDao.addItem(tweet2.getUserUuid(), tweet2);
        Set<Tweet> result = inMemoryDao.getItem(USER_UUID);

        // Then
        assertThat(result).containsExactly(TWEET, tweet2);
    }

    @Test
    public void addPostToTimelineSortByTimestampDesc() {
        //Given
        final Tweet tweet2 = Tweet.builder()
                .userUuid(USER_UUID)
                .uuid(UUID.randomUUID().toString())
                .text("Tweet 2")
                .timestamp(Instant.now().minusSeconds(1000L))
                .build();

        final Tweet tweet3 = Tweet.builder()
                .userUuid(USER_UUID)
                .uuid(UUID.randomUUID().toString())
                .text("Tweet 3")
                .timestamp(Instant.now().plusSeconds(1000L))
                .build();
        // When
        inMemoryDao.addItem(TWEET.getUserUuid(), TWEET);
        inMemoryDao.addItem(tweet2.getUserUuid(), tweet2);
        inMemoryDao.addItem(tweet3.getUserUuid(), tweet3);
        SortedSet<Tweet> result = inMemoryDao.getItem(USER_UUID);

        // Then
        assertThat(result).containsExactly(tweet3, TWEET, tweet2);
    }

    @Test
    public void addPostToTimelineMultipleUsers() {
        //Given
        final String userUuid2 = UUID.randomUUID().toString();
        final Tweet tweet2 = Tweet.builder()
                .userUuid(userUuid2)
                .uuid(UUID.randomUUID().toString())
                .text("Tweet 2")
                .timestamp(Instant.now().minusSeconds(1000L))
                .build();

        // When
        inMemoryDao.addItem(TWEET.getUserUuid(), TWEET);
        inMemoryDao.addItem(tweet2.getUserUuid(), tweet2);
        Set<Tweet> resultUser1 = inMemoryDao.getItem(USER_UUID);
        Set<Tweet> resultUser2 = inMemoryDao.getItem(userUuid2);

        // Then
        assertThat(resultUser1).containsExactly(TWEET);
        assertThat(resultUser2).containsExactly(tweet2);
    }

    @Test
    public void concurrencyTest() throws Exception {
        List<Tweet> tweets = IntStream.rangeClosed(0,10000).mapToObj(
                i -> Tweet.builder()
                        .userUuid(USER_UUID)
                        .uuid(UUID.randomUUID().toString())
                        .timestamp(Instant.now().plusSeconds(i*5))
                        .build()
        ).collect(Collectors.toList());

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        IntStream.rangeClosed(0, 10000).forEach( i ->
                executorService.execute(() ->
                        inMemoryDao.addItem(tweets.get(i).getUserUuid(), tweets.get(i))
                )
        );

        SortedSet<Tweet> result = inMemoryDao.getItem(USER_UUID);
        executorService.awaitTermination(1000L, TimeUnit.MILLISECONDS);
        assertThat(result.size()).isEqualTo(10001);
    }

}