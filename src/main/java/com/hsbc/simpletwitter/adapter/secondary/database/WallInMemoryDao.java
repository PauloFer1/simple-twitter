package com.hsbc.simpletwitter.adapter.secondary.database;

import com.hsbc.simpletwitter.core.model.Tweet;
import com.hsbc.simpletwitter.port.secondary.WallDao;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Named
public class WallInMemoryDao extends InMemoryDao<String, Tweet> implements WallDao {


    @Inject
    public WallInMemoryDao(final Comparator<Tweet> comparator,
                           final Map<String, ConcurrentSkipListSet<Tweet>> userWall) {
        super(comparator, userWall);
    }

    @Override
    public void insertPost(Tweet tweet) {
        super.addItem(tweet.getUserUuid(), tweet);
    }

    @Override
    public Optional<Set<Tweet>> retrieveWallByUser(String userUuid) {
        return Optional.ofNullable(super.getItem(userUuid));
    }
}
