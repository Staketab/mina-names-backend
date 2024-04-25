package com.staketab.minanames.entity;

import lombok.Getter;

@Getter
public enum ZkCloudWorkerTxOperation {
    ADD("add"),
    EXTEND("extend"),
    UPDATE("update"),
    REMOVE("remove");

    private final String name;

    ZkCloudWorkerTxOperation(String name) {
        this.name = name;
    }
}
