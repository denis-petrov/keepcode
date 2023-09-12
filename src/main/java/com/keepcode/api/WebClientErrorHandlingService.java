package com.keepcode.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service
public class WebClientErrorHandlingService {

    private static final Logger log = LoggerFactory.getLogger(WebClientErrorHandlingService.class);

    public Mono<? extends Throwable> handleErrorResponse(ClientResponse clientResponse) {
        HttpStatusCode httpStatusCode = clientResponse.statusCode();
        HttpStatus httpStatus = HttpStatus.valueOf(httpStatusCode.value());
        String errorMessage = "HTTP error " + httpStatus.value() + " : " + httpStatus.getReasonPhrase();
        log.error("Catch #{}, with message #{}", httpStatusCode.value(), errorMessage);
        return Mono.error(new WebClientResponseException(
                httpStatus.value(),
                errorMessage,
                clientResponse.headers().asHttpHeaders(),
                clientResponse.cookies().toString().getBytes(),
                null
        ));
    }

    public <T> Mono<List<T>> handleAnyErrors(Throwable ex, Runnable callback) {
        log.error("An error occurred while parsing #{}", ex.getMessage());
        callback.run();
        return Mono.just(Collections.emptyList());
    }
}
