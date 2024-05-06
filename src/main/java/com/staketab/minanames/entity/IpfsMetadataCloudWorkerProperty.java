package com.staketab.minanames.entity;

import lombok.Getter;

@Getter
public enum IpfsMetadataCloudWorkerProperty {
    IMAGE("image"),
    DESCRIPTION("description");

    private final String name;

    IpfsMetadataCloudWorkerProperty(String name) {
        this.name = name;
    }
}
