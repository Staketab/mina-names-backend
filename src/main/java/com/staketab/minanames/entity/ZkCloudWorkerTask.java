package com.staketab.minanames.entity;

import lombok.Getter;

@Getter
public enum ZkCloudWorkerTask {
    CREATE_TASK("createTxTask", "backend txTask", "execute"),
    SEND_TRANSACTIONS(null, "backend txs", "sendTransactions"),
    GET_BLOCK_INFO("getBlocksInfo", "commands info", "execute"),
    GET_DOMAIN_METADATA("getMetadata", "commands info", "execute");

    private final String name;
    private final String metadata;
    private final String command;

    ZkCloudWorkerTask(String name, String metadata, String command) {
        this.name = name;
        this.metadata = metadata;
        this.command = command;
    }
}
