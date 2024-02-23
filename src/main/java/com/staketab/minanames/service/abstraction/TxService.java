package com.staketab.minanames.service.abstraction;

import com.staketab.minanames.entity.PayableTransactionEntity;

public interface TxService {
    PayableTransactionEntity getOrCreate(String txHash);
}
