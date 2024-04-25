package com.staketab.minanames.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ZkCloudWorkerDataDTO {
    private String task;
    private List<ZkCloudWorkerTransaction> transactions;
    private String metadata;
    @Builder.Default
    private String args = "{\"contractAddress\":\"B62qrR3kE3S9xsQy2Jq8tp3TceWDeAmiXhU4KCXh19HzAVPj7BiNAME\"}";
    @Builder.Default
    private String repo = "nameservice";
    @Builder.Default
    private String developer = "@staketab";
    @Builder.Default
    private String mode = "sync";
}
