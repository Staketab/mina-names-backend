package com.staketab.minanames.dto;

import com.staketab.minanames.entity.DomainStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DomainDTO {

    private String id;
    private String ownerAddress;
    private String transaction;
    private String domainName;
    private BigDecimal amount;
    private Long reservationTimestamp;
    private Integer expirationTime;
    private Long startTimestamp;
    private Long endTimestamp;
    private DomainStatus domainStatus;
    private Boolean isSendToCloudWorker;
    private Boolean isDefault;
    private String ipfs;
    private OldMetadataDTO oldMetadata;
}
