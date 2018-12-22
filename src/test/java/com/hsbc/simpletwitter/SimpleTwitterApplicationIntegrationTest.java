package com.hsbc.simpletwitter;

import com.hsbc.simpletwitter.core.model.Timeline;
import com.hsbc.simpletwitter.core.model.Tweet;
import com.hsbc.simpletwitter.core.model.Wall;
import com.hsbc.simpletwitter.port.primary.controller.model.FollowerDto;
import com.hsbc.simpletwitter.port.primary.controller.model.TweetDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SimpleTwitterApplication.class},
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SimpleTwitterApplicationIntegrationTest {

    private static final String TWEET_URL = "/tweet";
    private static final String FOLLOW_URL = "/follow";
    private static final String WALL_URL = "/wall";
    private static final String TIMELINE_URL = "/timeline";
	private static final int TWEET_MAX_LENGTH = 140;
	private static final String USER_UUID = UUID.randomUUID().toString();
	private static final TweetDto CONTROLLER_TWEET = TweetDto.builder()
			.text("Tweet")
			.userUuid(USER_UUID)
			.build();
	private static final Tweet EXPECTED_TWEET = Tweet.builder()
			.text(CONTROLLER_TWEET.getText())
			.userUuid(USER_UUID)
			.build();

	@Autowired
	protected TestRestTemplate testRestTemplate;

	@Test
	public void postTweet() {
		// Given
		final HttpEntity<TweetDto> httpEntity = new HttpEntity<>(CONTROLLER_TWEET);

		// When
		ResponseEntity<Tweet> responseEntity = sendPostTweetRequest(httpEntity);

		// Then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).isEqualToComparingOnlyGivenFields(EXPECTED_TWEET, "text", "userUuid");
		assertThat(responseEntity.getBody().getTimestamp()).isBetween(Instant.now().minusSeconds(2L), Instant.now().plusSeconds(1L));
		assertThat(responseEntity.getBody().getUuid()).isNotEmpty();

	}

	@Test
	public void postTweetWithMoreThenMaxLengthCharsReturns400() {
		// Given
		final StringBuilder tweet = new StringBuilder();
		IntStream.range(0, TWEET_MAX_LENGTH + 1)
				.forEach(i -> tweet.append((char)i));
		final TweetDto tweetDto = TweetDto.builder()
				.text(tweet.toString())
				.userUuid(USER_UUID)
				.build();
		final HttpEntity<TweetDto> httpEntity = new HttpEntity<>(tweetDto);
		final String url = "/tweet";

		// When
		ResponseEntity<String> responseEntity = testRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

		// Then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(responseEntity.getBody()).contains("\"errorMessage\":\"Tweet Exceeded 140 characters for USER: " + USER_UUID + "\"");
	}

	@Test
	public void viewWall() {
		// Given
		final String userUuid = UUID.randomUUID().toString();
		final String url = WALL_URL + "/" + userUuid;
		final TweetDto tweetDto = TweetDto.builder()
				.text("Tweet")
				.userUuid(userUuid)
				.build();
		final Tweet expectedTweet = Tweet.builder()
				.text(tweetDto.getText())
				.userUuid(userUuid)
				.build();
		final HttpEntity<TweetDto> httpEntity = new HttpEntity<>(tweetDto);
		sendPostTweetRequest(httpEntity);

		// When
		ResponseEntity<Wall> responseEntity = testRestTemplate.getForEntity(url, Wall.class);

		// Then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody().getUserUuid()).isEqualTo(userUuid);
		assertThat(responseEntity.getBody().getTweets())
				.usingElementComparatorIgnoringFields("timestamp", "uuid")
				.containsExactly(expectedTweet);
	}

	@Test
	public void followUser() {
		// Given
		final String followee = UUID.randomUUID().toString();

		// When
		ResponseEntity<FollowerDto> responseEntity = sendPostFollow(USER_UUID, followee);

		// Then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody().getUserUuid()).isEqualTo(USER_UUID);
		assertThat(responseEntity.getBody().getUserUuidToFollow()).isEqualTo(followee);
	}

	@Test
	public void followUserSameIdsReturns400() {
		// Given
		final FollowerDto followerDto = FollowerDto.builder()
				.userUuid(USER_UUID)
				.userUuidToFollow(USER_UUID)
				.build();

		// When
		ResponseEntity<String> responseEntity = testRestTemplate.exchange(FOLLOW_URL, HttpMethod.POST, new HttpEntity<>(followerDto), String.class);

		// Then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(responseEntity.getBody()).contains("\"errorMessage\":\"User cannot follow himself: " + USER_UUID + "\"");
	}

	@Test
	public void viewTimelineBeforeFollow() {
		// Given
		final String follower = UUID.randomUUID().toString();
		final String url = TIMELINE_URL + "/" + follower;
		final HttpEntity<TweetDto> httpEntity = new HttpEntity<>(CONTROLLER_TWEET);
		sendPostTweetRequest(httpEntity);
		sendPostFollow(follower, USER_UUID);

		// When
		ResponseEntity<Timeline> responseEntity = testRestTemplate.getForEntity(url, Timeline.class);

		// Then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody().getUserUuid()).isEqualTo(follower);
		assertThat(responseEntity.getBody().getTweets()).isEmpty();
	}

	@Test
	public void viewTimelineAfterFollow() {
		// Given
		final String follower = UUID.randomUUID().toString();
        final String url = TIMELINE_URL + "/" + follower;
		final HttpEntity<TweetDto> httpEntity = new HttpEntity<>(CONTROLLER_TWEET);
		sendPostFollow(follower, USER_UUID);
		sendPostTweetRequest(httpEntity);

		// When
		ResponseEntity<Timeline> responseEntity = testRestTemplate.getForEntity(url, Timeline.class);

		// Then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody().getUserUuid()).isEqualTo(follower);
		assertThat(responseEntity.getBody().getTweets())
				.usingElementComparatorIgnoringFields("timestamp", "uuid")
				.containsExactly(EXPECTED_TWEET);
	}

	private ResponseEntity<Tweet> sendPostTweetRequest(final HttpEntity httpEntity) {
		return testRestTemplate.exchange(TWEET_URL, HttpMethod.POST, httpEntity, Tweet.class);
	}

	private ResponseEntity<FollowerDto> sendPostFollow(final String userUuid, final String followee) {
		final FollowerDto followerDto = FollowerDto.builder()
				.userUuid(userUuid)
				.userUuidToFollow(followee)
				.build();
		final HttpEntity<FollowerDto> httpEntity = new HttpEntity<>(followerDto);
		return testRestTemplate.exchange(FOLLOW_URL, HttpMethod.POST, httpEntity, FollowerDto.class);
	}

}

