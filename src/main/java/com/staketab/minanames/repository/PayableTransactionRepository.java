package com.staketab.minanames.repository;

import com.staketab.minanames.entity.PayableTransactionEntity;
import com.staketab.minanames.entity.dto.TxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayableTransactionRepository extends JpaRepository<PayableTransactionEntity, String> {

    List<PayableTransactionEntity> findAllByTxStatus(TxStatus txStatus);
}
