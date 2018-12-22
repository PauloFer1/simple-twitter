package com.hsbc.simpletwitter.adapter.secondary.database.comparator;

import com.hsbc.simpletwitter.core.model.Tweet;

import javax.inject.Named;
import java.util.Comparator;

@Named
public class PostComparator implements Comparator<Tweet> {

    @Override
    public int compare(Tweet o1, Tweet o2) {
        return o2.getTimestamp().compareTo(o1.getTimestamp());
    }
}
