package com.staketab.minanames.service;

import com.staketab.minanames.dto.IpfsDomainMetadataZkDataDTO;
import com.staketab.minanames.dto.ZkCloudWorkerContractDataResponse;
import com.staketab.minanames.entity.PayableTransactionEntity;

import java.util.List;

public interface ZkCloudWorkerService {

    void sendTxs(List<PayableTransactionEntity> appliedTxs);

    ZkCloudWorkerContractDataResponse getBlockInfo(String startBlock);

    void sendCreateTask();

    void checkBlocksFromZkCloudWorker();

    IpfsDomainMetadataZkDataDTO getDomainMetadata(String domainMetadata);
}
