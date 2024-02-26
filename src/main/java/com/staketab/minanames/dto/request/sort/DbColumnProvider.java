package com.staketab.minanames.dto.request.sort;

@FunctionalInterface
public interface DbColumnProvider {
    String getDbColumn();
}
