package com.staketab.minanames.service;

import com.staketab.minanames.entity.PayableTransactionEntity;

import java.util.List;

public interface ZkCloudWorkerService {

    void sendTxs(List<PayableTransactionEntity> appliedTxs);

    void sendCreateTask();
}
