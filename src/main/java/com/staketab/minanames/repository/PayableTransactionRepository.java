package com.staketab.minanames.repository;

import com.staketab.minanames.entity.PayableTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayableTransactionRepository extends JpaRepository<PayableTransactionEntity, String> {
}
