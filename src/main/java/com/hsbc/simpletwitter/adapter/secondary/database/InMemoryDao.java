package com.hsbc.simpletwitter.adapter.secondary.database;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

public class InMemoryDao<T,O> {

    private final Comparator<O> comparator;
    private final Map<T, ConcurrentSkipListSet<O>> cache;

    InMemoryDao(Comparator<O> comparator, Map<T, ConcurrentSkipListSet<O>> cache) {
        this.comparator = comparator;
        this.cache = cache;
    }

    void addItem(final T key, final O value) {
        cache.compute(key, (k, v) -> {
            if (null == v) {
                v = new ConcurrentSkipListSet<>(comparator);
            }
            v.add(value);
            return v;
        });
    }

    ConcurrentSkipListSet<O> getItem(final T key) {
        return cache.get(key);
    }
}
