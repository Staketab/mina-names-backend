package com.staketab.minanames.dto;

import lombok.Data;

@Data
public class ZkCloudWorkerRequestDTO {
    private String auth;
    private String jwtToken;
    private String command;
    private ZkCloudWorkerDataDTO data;
    private String chain;
}
