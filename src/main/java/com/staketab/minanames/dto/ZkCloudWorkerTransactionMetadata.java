package com.staketab.minanames.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZkCloudWorkerTransactionMetadata {
    private String ipfsImgUrl;
    private String website;
    private String email;
    private String discord;
    private String github;
    private String xTwitter;
    private String telegram;
    private String description;
}
