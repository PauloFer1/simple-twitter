package com.hsbc.simpletwitter.adapter.secondary.database;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class FollowersInMemoryDaoTest {

    private static final String USER_UUID = UUID.randomUUID().toString();
    private static final String FOLLOWER_UUID = UUID.randomUUID().toString();

    private FollowersInMemoryDao followersInMemoryDao;

    @Before
    public void SetUp() {
        followersInMemoryDao = new FollowersInMemoryDao(new ConcurrentHashMap<>());
    }

    @Test
    public void addFollowerWithEmptySet() {
        // Given
        // When
        followersInMemoryDao.addFollower(USER_UUID, FOLLOWER_UUID);

        // Then
        Optional<Set<String>> followers = followersInMemoryDao.getFollowers(USER_UUID);
        assertThat(followers.get()).containsExactly(FOLLOWER_UUID);
    }

    @Test
    public void addFollowerMultipleSameUser() {
        // Given
        final String follwerUuid2 = UUID.randomUUID().toString();
        // When
        followersInMemoryDao.addFollower(USER_UUID, FOLLOWER_UUID);
        followersInMemoryDao.addFollower(USER_UUID, follwerUuid2);

        // Then
        Optional<Set<String>> followers = followersInMemoryDao.getFollowers(USER_UUID);
        assertThat(followers.get()).containsExactlyInAnyOrder(FOLLOWER_UUID, follwerUuid2);
    }

    @Test
    public void getFollowersWithEmptySet() {
        // Given
        // When
        Optional<Set<String>> followers = followersInMemoryDao.getFollowers(USER_UUID);

        // Then
        assertThat(followers).isEmpty();
    }
}