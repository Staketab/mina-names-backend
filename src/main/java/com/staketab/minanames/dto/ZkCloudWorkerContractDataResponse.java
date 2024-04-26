package com.staketab.minanames.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ZkCloudWorkerContractDataResponse {
    private String contractAddress;
    private String startBlock;
    private ZkCloudWorkerContractStateResponse contractState;
    private List<ZkCloudWorkerBlocksResponse> blocks;
}
