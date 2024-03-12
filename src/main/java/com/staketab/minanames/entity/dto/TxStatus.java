package com.staketab.minanames.entity.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TxStatus {
    PENDING,
    APPLIED,
    FAILED
}
