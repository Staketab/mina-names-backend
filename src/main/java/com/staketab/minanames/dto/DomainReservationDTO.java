package com.staketab.minanames.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainReservationDTO {
    private String ownerAddress;
    private String domainName;
    private Integer expirationTime;
    private Double amount;
}
