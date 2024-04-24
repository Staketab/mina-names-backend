package com.staketab.minanames.service;

import com.staketab.minanames.entity.PayableTransactionEntity;
import com.staketab.minanames.entity.TxStatus;

import java.util.List;

public interface TxService {
    PayableTransactionEntity getOrCreate(String txHash, int countDomains, TxStatus status);

    void checkTransactions();

    void deleteTxsWithIncorrectAmount();

    void deleteTxs(List<String> txHashes);
}
