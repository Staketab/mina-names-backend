package com.staketab.minanames.entity;

import lombok.Getter;

@Getter
public enum ZkCloudWorkerTask {
    SEND_TRANSACTION("createTxTask", "backend txs"),
    GET_BLOCK_INFO("getBlocksInfo", "commands info");

    private final String name;
    private final String metadata;

    ZkCloudWorkerTask(String name, String metadata) {
        this.name = name;
        this.metadata = metadata;
    }
}
