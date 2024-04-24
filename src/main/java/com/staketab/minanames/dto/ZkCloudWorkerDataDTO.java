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
    private String args = "{\"contractAddress\":\"B62qmyBYvHL5g7os2HFcGJC1QASTkFC8ydUBZRKGrxDqhV853YoNAME\"}";
    @Builder.Default
    private String repo = "nameservice";
    @Builder.Default
    private String developer = "@staketab";
    @Builder.Default
    private String mode = "sync";
}
