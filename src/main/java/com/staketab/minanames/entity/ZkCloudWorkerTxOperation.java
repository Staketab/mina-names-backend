package com.staketab.minanames.entity;

import lombok.Getter;

import java.util.Arrays;

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

    public static ZkCloudWorkerTxOperation fromString(String name){
        return Arrays.stream(ZkCloudWorkerTxOperation.values())
                .filter(v -> name.equals(v.getName()))
                .findFirst()
                .orElseThrow();
    }
}
