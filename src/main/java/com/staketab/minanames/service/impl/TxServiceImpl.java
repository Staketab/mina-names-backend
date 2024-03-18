package com.staketab.minanames.service.impl;

import com.staketab.minanames.entity.PayableTransactionEntity;
import com.staketab.minanames.entity.dto.TxStatus;
import com.staketab.minanames.minascan.TransactionRepository;
import com.staketab.minanames.minascan.TxProjection;
import com.staketab.minanames.repository.DomainRepository;
import com.staketab.minanames.repository.PayableTransactionRepository;
import com.staketab.minanames.service.abstraction.TxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TxServiceImpl implements TxService {

    private final PayableTransactionRepository payableTransactionRepository;
    private final TransactionRepository transactionRepository;
    private final DomainRepository domainRepository;

    @Override
    public PayableTransactionEntity getOrCreate(String txHash) {
        return payableTransactionRepository.findById(txHash).orElseGet(() -> createTx(txHash));
    }

    @Override
    @Transactional
    public void checkTransactions() {
        List<PayableTransactionEntity> pendingTxs = payableTransactionRepository.findAllByTxStatus(TxStatus.PENDING);
        Map<TxStatus, List<PayableTransactionEntity>> txStatusListMap = generateMapOfFailedAndAppliedTxs(pendingTxs);

        applyReservedTxs(txStatusListMap.get(TxStatus.APPLIED));
        domainRepository.deleteAllByTransactionIn(txStatusListMap.get(TxStatus.FAILED));
    }

    //todo set amount column
    private void applyReservedTxs(List<PayableTransactionEntity> pendingTxs) {
        List<PayableTransactionEntity> appliedTxs = pendingTxs.stream()
                .peek(payableTransaction -> payableTransaction.setTxStatus(TxStatus.APPLIED))
                .toList();

        payableTransactionRepository.saveAll(appliedTxs);
    }

    private Map<TxStatus, List<PayableTransactionEntity>> generateMapOfFailedAndAppliedTxs(List<PayableTransactionEntity> payableTransactions) {
        Map<String, PayableTransactionEntity> mapPayableTransactions = payableTransactions.stream()
                .collect(Collectors.toMap(PayableTransactionEntity::getTxHash, Function.identity()));
        List<TxProjection> txStatusProjections = transactionRepository.getTxStatusByTxHashIn(mapPayableTransactions.keySet());
        return getSplitedMap(txStatusProjections, mapPayableTransactions);
    }

    private static Map<TxStatus, List<PayableTransactionEntity>> getSplitedMap(List<TxProjection> txStatusByTxHashIn, Map<String, PayableTransactionEntity> mapPayableTransactions) {
        List<PayableTransactionEntity> failed = new ArrayList<>();
        List<PayableTransactionEntity> applied = new ArrayList<>();
        for (TxProjection txProjection : txStatusByTxHashIn) {
            if (TxStatus.APPLIED.name().equals(txProjection.getStatus().toUpperCase())) {
                applied.add(mapPayableTransactions.get(txProjection.getTxHash()));
            }
            if (TxStatus.FAILED.name().equals(txProjection.getStatus().toUpperCase())) {
                failed.add(mapPayableTransactions.get(txProjection.getTxHash()));
            }
        }
        return Map.of(TxStatus.APPLIED, applied, TxStatus.FAILED, failed);
    }

    private PayableTransactionEntity createTx(String txHash) {
        return payableTransactionRepository.save(PayableTransactionEntity.builder()
                .txHash(txHash)
                .txStatus(TxStatus.PENDING)
                .build());
    }
}
