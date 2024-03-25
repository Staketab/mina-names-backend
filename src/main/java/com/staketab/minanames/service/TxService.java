package com.staketab.minanames.service;

import com.staketab.minanames.entity.PayableTransactionEntity;

public interface TxService {
    PayableTransactionEntity getOrCreate(String txHash);

    void checkTransactions();
}
