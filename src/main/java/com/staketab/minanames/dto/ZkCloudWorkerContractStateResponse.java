package com.staketab.minanames.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ZkCloudWorkerContractStateResponse {
    private String domain;
    private ZkCloudWorkerBlocksProviderResponse lastCreatedBlock;
    private ZkCloudWorkerBlocksProviderResponse lastValidatedBlock;
    private ZkCloudWorkerBlocksProviderResponse lastProvedBlock;
    private ZkCloudWorkerValidatorsResponse validators;
}
