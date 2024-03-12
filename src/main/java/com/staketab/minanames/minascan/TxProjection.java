package com.staketab.minanames.minascan;

public interface TxProjection {

    String getTxHash();

    String getStatus();

    Long getAmount();
}
