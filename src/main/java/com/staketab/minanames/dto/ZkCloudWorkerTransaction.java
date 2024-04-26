package com.staketab.minanames.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZkCloudWorkerTransaction {
    private String operation;
    private String name;
    private String address;
    private long expiry;
    private String metadata;
    private OldDomain oldDomain;
    private String signature;
}
