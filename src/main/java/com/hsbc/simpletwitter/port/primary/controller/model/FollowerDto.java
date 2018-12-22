package com.hsbc.simpletwitter.port.primary.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowerDto {
    private String userUuid;
    private String userUuidToFollow;
}