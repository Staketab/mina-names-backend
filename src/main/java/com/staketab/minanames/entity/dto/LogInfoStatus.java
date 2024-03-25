package com.staketab.minanames.entity.dto;

import lombok.Getter;

@Getter
public enum LogInfoStatus {
    CREATE("Create new domain"),
    FAILED("Failed tx"),
    INCORRECT_AMOUNT("Incorrect amount"),
    APPLIED("Applied tx"),
    REMOVE_RESERVATION("Remove reservation");

    private final String message;

    LogInfoStatus(String message) {
        this.message = message;
    }
}
