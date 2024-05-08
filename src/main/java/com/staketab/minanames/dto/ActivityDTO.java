package com.staketab.minanames.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityDTO {

    private String id;
    private String domainName;
    private String ownerAddress;
    private String transaction;
    private String activity;
    private String details;
    private long timestamp;
}
