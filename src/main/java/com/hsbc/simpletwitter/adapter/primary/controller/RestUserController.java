package com.hsbc.simpletwitter.adapter.primary.controller;

import com.hsbc.simpletwitter.core.domain.FanOutService;
import com.hsbc.simpletwitter.core.domain.UserTweetService;
import com.hsbc.simpletwitter.core.model.Tweet;
import com.hsbc.simpletwitter.core.model.Timeline;
import com.hsbc.simpletwitter.core.model.Wall;
import com.hsbc.simpletwitter.port.primary.controller.model.FollowerDto;
import com.hsbc.simpletwitter.port.primary.controller.model.TweetDto;
import com.hsbc.simpletwitter.port.primary.controller.UserController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RestUserController implements UserController<ResponseEntity> {

    private final UserTweetService userTweetService;
    private final FanOutService fanOutService;

    @ApiOperation("Post a Tweet")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Exception Occurred"),
    })
    @RequestMapping(value = "/tweet", method = RequestMethod.POST, produces = "application/json")
    @Override
    public ResponseEntity<Tweet> postTweet(@RequestBody final TweetDto tweet) {
        log.info("POST /tweet called with: {}", tweet);
        return ResponseEntity.ok(fanOutService.postTweet(tweet.getUserUuid(), tweet.getText()));
    }

    @ApiOperation("Follow User")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Exception Occurred"),
    })
    @RequestMapping(value = "/follow", method = RequestMethod.POST, produces = "application/json")
    @Override
    public ResponseEntity<FollowerDto> followUser(@RequestBody final FollowerDto followerDto) {
        log.info("POST /follow called with: {}", followerDto);
        fanOutService.followUser(followerDto.getUserUuid(), followerDto.getUserUuidToFollow());
        return ResponseEntity.ok(followerDto);
    }

    @ApiOperation("Get the User Wall")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Exception Occurred"),
    })
    @RequestMapping(value = "/wall/{userUuid}", method = RequestMethod.GET, produces = "application/json")
    @Override
    public ResponseEntity<Wall> viewWall(@PathVariable("userUuid") final String userUuid) {
        log.info("GET /wall called with: {}", userUuid);
        return ResponseEntity.ok(Wall.builder().userUuid(userUuid).tweets(userTweetService.getUserWall(userUuid)).build());
    }

    @ApiOperation("Get the User Timeline")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Exception Occurred"),
    })
    @RequestMapping(value = "/timeline/{userUuid}", method = RequestMethod.GET, produces = "application/json")
    @Override
    public ResponseEntity<Timeline> viewTimeline(@PathVariable("userUuid") final String userUuid) {
        log.info("GET /timeline called with: {}", userUuid);
        return ResponseEntity.ok(Timeline.builder().userUuid(userUuid).tweets(userTweetService.getUserTimeline(userUuid)).build());
    }
}
