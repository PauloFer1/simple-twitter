package com.hsbc.simpletwitter.adapter.secondary.database;

import com.hsbc.simpletwitter.core.model.Tweet;
import com.hsbc.simpletwitter.port.secondary.TimelineDao;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Named
public class TimelineInMemoryDao extends InMemoryDao<String, Tweet> implements TimelineDao {

    @Inject
    public TimelineInMemoryDao(final Comparator<Tweet> comparator,
                               final Map<String, ConcurrentSkipListSet<Tweet>> userTimeline) {
        super(comparator, userTimeline);
    }


    @Override
    public Optional<Set<Tweet>> retrieveTimelineByUser(final String userUuid) {
        return Optional.ofNullable(super.getItem(userUuid));
    }

    @Override
    public void addPostToTimeline(final String userUuid, final Tweet followeeTweet) {
       super.addItem(userUuid, followeeTweet);
    }
}
