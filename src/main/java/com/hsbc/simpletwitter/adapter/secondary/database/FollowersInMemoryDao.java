package com.hsbc.simpletwitter.adapter.secondary.database;

import com.hsbc.simpletwitter.port.secondary.FollowersDao;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Named
public class FollowersInMemoryDao implements FollowersDao {

    private final ConcurrentHashMap<String, Set<String>> followers;

    @Inject
    public FollowersInMemoryDao(ConcurrentHashMap<String, Set<String>> followers) {
        this.followers = followers;
    }

    @Override
    public Optional<Set<String>> getFollowers(String userUuid) {
        return Optional.ofNullable(followers.get(userUuid));
    }


    @Override
    public void addFollower(String userUuid, String followerUuid) {
        followers.compute(userUuid, (key, values) -> {
            if (null == values) {
                values = new HashSet<>(Collections.singleton(followerUuid));
            } else {
                values.add(followerUuid);
            }
            return values;
        });
    }
}
