package com.staketab.minanames.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ZkCloudWorkerBlocksResponse {
    private int blockNumber;
    private String blockAddress;
    private String root;
    private String ipfs;
    private boolean isValidated;
    private boolean isInvalid;
    private boolean isProved;
    private boolean isFinal;
    private String timeCreated;
    private String txsCount;
    private int invalidTxsCount;
    private String txsHash;
    private String previousBlockAddress;
}
