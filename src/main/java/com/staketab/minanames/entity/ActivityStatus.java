package com.staketab.minanames.entity;

import lombok.Getter;

@Getter
public enum ActivityStatus {
    CREATE("Create new domain", null, false),
    CART_RESERVE("Reserve new domain for cart", null, false),
    DELETE_CART_RESERVE("Remove reserved domain for cart", null, false),
    APPLY_CART_RESERVED_DOMAINS("Apply cart reserved domains", null, false),
    SEND_DOMAIN_TO_ZK_CLOUD_WORKER("Send domain to zk cloud worker", null, false),
    SET_ACTIVE_STATUS_FOR_DOMAIN("Buy", "Price: %s MINA", true),
    UPDATE_DOMAIN_IMAGE("Update record", "Image updated", true),
    UPDATE_DOMAIN_DESCRIPTION("Update record", "Description updated", true),
    FAILED("Failed tx", null, false),
    INCORRECT_AMOUNT("Incorrect amount", null, false),
    APPLIED("Applied tx", null, false),
    REMOVE_RESERVATION("Remove reservation", null, false),
    SET_DEFAULT_DOMAIN("Set as default domain", null, true),
    REMOVE_CART_RESERVATION("Remove cart reservation", null, false);

    private final String message;
    private final String details;
    private final boolean isShow;

    ActivityStatus(String message, String details, boolean isShow) {
        this.message = message;
        this.details = details;
        this.isShow = isShow;
    }
}
