package com.staketab.minanames.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IpfsZkCloudWorkerTransactionDataResponse {
    private String operation;
    private String name;
    private String address;
    private long expiry;
    private String newDomain;
    private String oldDomain;
    private String metadata;
}
