package com.staketab.minanames.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZkCloudWorkerRequestDTO {
    private String auth;
    private String jwtToken;
    @Builder.Default
    private String command = "execute";
    private ZkCloudWorkerDataDTO data;
    private String chain;
}
