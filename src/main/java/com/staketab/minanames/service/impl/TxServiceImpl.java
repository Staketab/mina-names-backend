package com.staketab.minanames.service.impl;

import com.staketab.minanames.entity.PayableTransactionEntity;
import com.staketab.minanames.entity.dto.TxStatus;
import com.staketab.minanames.minascan.TransactionRepository;
import com.staketab.minanames.repository.PayableTransactionRepository;
import com.staketab.minanames.service.abstraction.TxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TxServiceImpl implements TxService {

    private final PayableTransactionRepository payableTransactionRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public PayableTransactionEntity getOrCreate(String txHash) {
        return payableTransactionRepository.findById(txHash).orElseGet(() -> createTx(txHash));
    }

    @Override
    public void checkTransactions() {
        List<PayableTransactionEntity> resultTxs = payableTransactionRepository
                .findAllByTxStatus(TxStatus.PENDING)
                .stream()
                .filter(this::txStatusPredicate)
                .peek(payableTransaction -> payableTransaction.setTxStatus(TxStatus.APPLIED))
                .toList();

        payableTransactionRepository.saveAll(resultTxs);
    }

    private boolean txStatusPredicate(PayableTransactionEntity payableTransaction) {
        String txHash = payableTransaction.getTxHash();
        String txStatusByTxHash = transactionRepository.getTxStatusByTxHash(txHash);
        return txStatusByTxHash != null && TxStatus.APPLIED.name().equals(txStatusByTxHash.toUpperCase());
    }

    private PayableTransactionEntity createTx(String txHash) {
        return payableTransactionRepository.save(PayableTransactionEntity.builder()
                .txHash(txHash)
                .txStatus(TxStatus.PENDING)
                .build());
    }
}
