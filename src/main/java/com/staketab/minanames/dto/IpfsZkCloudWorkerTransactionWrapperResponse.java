package com.staketab.minanames.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IpfsZkCloudWorkerTransactionWrapperResponse {
    private IpfsZkCloudWorkerTransactionResponse tx;
    private IpfsZkCloudWorkerTransactionFieldResponse fields;
}
