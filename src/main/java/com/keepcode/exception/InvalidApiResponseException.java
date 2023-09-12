package com.keepcode.exception;

public class InvalidApiResponseException extends RuntimeException {
    public InvalidApiResponseException(String message) {
        super(message);
    }
}
