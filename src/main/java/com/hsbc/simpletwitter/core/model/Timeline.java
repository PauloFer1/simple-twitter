package com.hsbc.simpletwitter.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Timeline {
    private String userUuid;
    private Set<Tweet> tweets;
}
