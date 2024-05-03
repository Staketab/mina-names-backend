package com.staketab.minanames.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IpfsZkCloudWorkerResponse {
    private int blockNumber;
    private String timeCreated;
    private String contractAddress;
    private String blockAddress;
    private String root;
    private String blockProducer;
    private String chainId;
    private String txsCount;
    private int invalidTxsCount;
    private String txsHash;
    private String previousBlockAddress;
    private String previousValidBlockAddress;
    private String oldRoot;
    private List<IpfsZkCloudWorkerTransactionWrapperResponse> transactions;
    private String map;
}
