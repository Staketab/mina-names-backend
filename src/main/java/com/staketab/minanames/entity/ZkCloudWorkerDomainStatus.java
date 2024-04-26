package com.staketab.minanames.entity;

import lombok.Getter;

@Getter
public enum ZkCloudWorkerDomainStatus {
    ACCEPTED("accepted");

    private final String name;

    ZkCloudWorkerDomainStatus(String name) {
        this.name = name;
    }
}
