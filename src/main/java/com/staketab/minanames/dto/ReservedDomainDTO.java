package com.staketab.minanames.dto;

import com.staketab.minanames.entity.DomainStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservedDomainDTO {

    private String id;
    private DomainStatus status;
}
