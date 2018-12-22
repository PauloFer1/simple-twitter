package com.hsbc.simpletwitter.core.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class Wall {
    private final String userUuid;
    private final Set<Tweet> tweets;
}
