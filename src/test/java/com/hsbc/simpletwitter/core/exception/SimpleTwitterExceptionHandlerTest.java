package com.hsbc.simpletwitter.core.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SimpleTwitterExceptionHandlerTest {

    private static final int TWEET_MAX_LENGTH = 140;

    @Mock
    private WebRequest webRequest;

    private SimpleTwitterExceptionHandler simpleTwitterExceptionHandler = new SimpleTwitterExceptionHandler();

    @Test
    public void exceptionHandlerMaxTweetCharactersException() {
        // Given
        final String userUuid = UUID.randomUUID().toString();
        final MaxTweetCharactersException maxTweetCharactersException = new MaxTweetCharactersException(userUuid, TWEET_MAX_LENGTH);

        // When
        ResponseEntity<SimpleTwitterExceptionHandler.ErrorResponse> responseEntity =
                simpleTwitterExceptionHandler.exceptionHandler(maxTweetCharactersException, webRequest);

        // Then
        assertThat(responseEntity.getBody().getHttpCode()).isEqualTo(400);
        assertThat(responseEntity.getBody().getErrorMessage()).isEqualTo(maxTweetCharactersException.getMessage());
        assertThat(responseEntity.getBody().getTimestamp()).isBetween(Instant.now().minusSeconds(5L), Instant.now().plusSeconds(1L));
    }

    @Test
    public void exceptionHandlerUserCannotFollowHimselfException() {
        // Given
        final String userUuid = UUID.randomUUID().toString();
        final UserCannotFollowHimselfException userCannotFollowHimselfException = new UserCannotFollowHimselfException(userUuid);

        // When
        ResponseEntity<SimpleTwitterExceptionHandler.ErrorResponse> responseEntity =
                simpleTwitterExceptionHandler.exceptionHandler(userCannotFollowHimselfException, webRequest);

        // Then
        assertThat(responseEntity.getBody().getHttpCode()).isEqualTo(400);
        assertThat(responseEntity.getBody().getErrorMessage()).isEqualTo(userCannotFollowHimselfException.getMessage());
        assertThat(responseEntity.getBody().getTimestamp()).isBetween(Instant.now().minusSeconds(5L), Instant.now().plusSeconds(1L));
    }

}