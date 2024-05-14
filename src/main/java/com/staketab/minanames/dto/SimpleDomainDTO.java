package com.staketab.minanames.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimpleDomainDTO {

    private String id;
    private String ownerAddress;
    private String ipfs;
    private String domainName;
    private String domainImg;
    private long timestamp;
    private Boolean isDefault;
    private String status;
}
