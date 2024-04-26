package com.staketab.minanames.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZkCloudWorkerDataDTO {
    private String task;
    private List<String> transactions;
    private String metadata;
    @Builder.Default
    private String args = "{\"contractAddress\":\"B62qrjWrAaXV65CZgpfhLdFynbFdyj851cWZPCPvF92mF3ohGDbNAME\"}";
    @Builder.Default
    private String repo = "nameservice";
    @Builder.Default
    private String developer = "@staketab";
    @Builder.Default
    private String mode = "sync";
}
