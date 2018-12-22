package com.hsbc.simpletwitter.port.secondary;

import com.hsbc.simpletwitter.core.model.Tweet;

import java.util.Optional;
import java.util.Set;

public interface WallDao {
    Optional<Set<Tweet>> retrieveWallByUser(final String userUuid);
    void insertPost(final Tweet tweet);
}
