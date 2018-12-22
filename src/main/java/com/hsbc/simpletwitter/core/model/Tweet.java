package com.hsbc.simpletwitter.core.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Tweet {
    private final String uuid;
    private final String userUuid;
    private final String text;
    private final Instant timestamp;
}
