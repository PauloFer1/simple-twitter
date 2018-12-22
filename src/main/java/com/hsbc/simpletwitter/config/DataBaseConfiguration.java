package com.hsbc.simpletwitter.config;

import com.hsbc.simpletwitter.core.model.Tweet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Configuration
public class DataBaseConfiguration {

    @Bean
    public Map<String, ConcurrentSkipListSet<Tweet>> userTimeline() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<String, ConcurrentSkipListSet<Tweet>> userWall() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentHashMap<String, Set<String>> followers() {
        return new ConcurrentHashMap<>();
    }
}
