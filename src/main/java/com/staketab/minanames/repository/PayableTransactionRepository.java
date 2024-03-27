package com.staketab.minanames.repository;

import com.staketab.minanames.entity.PayableTransactionEntity;
import com.staketab.minanames.entity.TxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PayableTransactionRepository extends JpaRepository<PayableTransactionEntity, String> {

    List<PayableTransactionEntity> findAllByTxStatus(TxStatus txStatus);

    @Modifying
    void deleteAllByTxHashIn(Collection<String> txHash);

}
