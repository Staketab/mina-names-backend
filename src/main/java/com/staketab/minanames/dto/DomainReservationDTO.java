package com.staketab.minanames.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DomainReservationDTO {
    private String ownerAddress;
    private String domainName;
    private Integer expirationTime;
    private Double amount;
    private String txHash;
}
