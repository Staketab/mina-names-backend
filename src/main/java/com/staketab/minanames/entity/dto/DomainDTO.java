package com.staketab.minanames.entity.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DomainDTO {

    private String id;
    private String ownerAddress;
    private String transaction;
    private String domainImg;
    private BigDecimal amount;
    private Long reservationTimestamp;
    private Integer expirationTime;
    private Long startTimestamp;
    private DomainStatus domainStatus;
    private Boolean isSendToCloudWorker;
    private Boolean isDefault;
}
