package com.staketab.minanames.entity;

import lombok.Getter;

@Getter
public enum LogInfoStatus {
    CREATE("Create new domain"),
    SAVE_TX_FOR_ONE_DOMAIN("Save tx hash for domain"),
    SAVE_TX_FOR_CARTS_DOMAIN("Save tx hash for carts domain"),
    CART_RESERVE("Reserve new domain for cart"),
    DELETE_CART_RESERVE("Remove reserved domain for cart"),
    APPLY_CART_RESERVED_DOMAINS("Apply cart reserved domains"),
    FAILED("Failed tx"),
    INCORRECT_AMOUNT("Incorrect amount"),
    APPLIED("Applied tx"),
    REMOVE_RESERVATION("Remove reservation"),
    REMOVE_CART_RESERVATION("Remove cart reservation");

    private final String message;

    LogInfoStatus(String message) {
        this.message = message;
    }
}
