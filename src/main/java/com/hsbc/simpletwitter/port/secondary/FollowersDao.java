package com.hsbc.simpletwitter.port.secondary;

import java.util.Optional;
import java.util.Set;

public interface FollowersDao {
    Optional<Set<String>> getFollowers(final String userUuid);
    void addFollower(final String userUuid, final String followerUuid);
}
