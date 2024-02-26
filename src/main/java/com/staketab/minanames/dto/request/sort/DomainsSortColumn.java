package com.staketab.minanames.dto.request.sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DomainsSortColumn implements DbColumnProvider {
    AMOUNT("amount"),
    STATUS("status"),
    RESERVATION_TIMESTAMP("reservation_timestamp"),
    IS_SEND_TO_CLOUD_WORKER("is_send_to_cloud_worker");

    private final String dbColumn;
}
