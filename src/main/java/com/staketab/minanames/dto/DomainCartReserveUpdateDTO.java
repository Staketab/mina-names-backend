package com.staketab.minanames.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DomainCartReserveUpdateDTO {

    private String id;
    private Integer expirationTime;
    private Double amount;
}
