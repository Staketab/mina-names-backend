package com.staketab.minanames.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TxStatus {
    PENDING,
    APPLIED,
    FAILED,
    RESERVED
}
