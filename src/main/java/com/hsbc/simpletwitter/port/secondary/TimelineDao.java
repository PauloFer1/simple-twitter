package com.hsbc.simpletwitter.port.secondary;

import com.hsbc.simpletwitter.core.model.Tweet;

import java.util.Optional;
import java.util.Set;

public interface TimelineDao {
    Optional<Set<Tweet>> retrieveTimelineByUser(final String userUuid);
    void addPostToTimeline(final String userUuid, final Tweet followeeTweet);
}
