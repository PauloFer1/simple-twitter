package com.hsbc.simpletwitter.core.domain;

import com.hsbc.simpletwitter.core.model.Tweet;
import com.hsbc.simpletwitter.port.secondary.TimelineDao;
import com.hsbc.simpletwitter.port.secondary.WallDao;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import java.util.Collections;
import java.util.Set;

@Named
@RequiredArgsConstructor
public class UserTweetService {

    private final TimelineDao timelineDao;
    private final WallDao wallDao;

    public Set<Tweet> getUserTimeline(final String userUuid) {
        return timelineDao.retrieveTimelineByUser(userUuid).orElse(Collections.emptySet());
    }

    public Set<Tweet> getUserWall(final String userUuid) {
        return wallDao.retrieveWallByUser(userUuid).orElse(Collections.emptySet());
    }
}
