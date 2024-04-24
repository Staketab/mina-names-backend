package com.staketab.minanames.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ZkCloudWorkerTransaction {
    private String operation;
    private String name;
    private String address;
    private long expiry;
    private String metadata;
    private OldDomain oldDomain;
    private String signature;
}
