package com.staketab.minanames.dto.request.sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActivitySortColumn implements DbColumnProvider {
    TIMESTAMP("timestamp");

    private final String dbColumn;
}
