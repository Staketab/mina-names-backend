package com.staketab.minanames.exception;

import org.springframework.http.HttpStatusCode;

public record ApiError(String statusCode, String message) {
    public ApiError(HttpStatusCode statusCode, String message) {
        this(statusCode.toString(), message);
    }
}
