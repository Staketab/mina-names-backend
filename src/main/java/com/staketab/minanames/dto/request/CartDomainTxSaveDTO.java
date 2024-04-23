package com.staketab.minanames.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDomainTxSaveDTO {
    private String ownerAddress;
    private String txHash;
    private List<String> domains;
}
