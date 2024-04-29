package com.staketab.minanames.service;

import com.staketab.minanames.dto.ZkCloudWorkerContractDataResponse;
import com.staketab.minanames.entity.PayableTransactionEntity;

import java.util.List;

public interface ZkCloudWorkerService {

    void sendTxs(List<PayableTransactionEntity> appliedTxs);

    ZkCloudWorkerContractDataResponse getBlockInfo();

    void sendCreateTask();

    void checkBlocksFromZkCloudWorker();
}
