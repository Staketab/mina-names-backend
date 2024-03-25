package com.staketab.minanames.service.impl;

import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.entity.LogInfoEntity;
import com.staketab.minanames.entity.PayableTransactionEntity;
import com.staketab.minanames.entity.dto.LogInfoStatus;
import com.staketab.minanames.entity.dto.TxStatus;
import com.staketab.minanames.minascan.TransactionRepository;
import com.staketab.minanames.minascan.TxProjection;
import com.staketab.minanames.repository.DomainRepository;
import com.staketab.minanames.repository.PayableTransactionRepository;
import com.staketab.minanames.service.LogInfoService;
import com.staketab.minanames.service.TxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.staketab.minanames.entity.dto.LogInfoStatus.APPLIED;
import static com.staketab.minanames.entity.dto.LogInfoStatus.FAILED;
import static com.staketab.minanames.entity.dto.LogInfoStatus.INCORRECT_AMOUNT;

@Slf4j
@Service
@RequiredArgsConstructor
public class TxServiceImpl implements TxService {

    private final LogInfoService logInfoService;
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
        List<PayableTransactionEntity> appliedTxs = removeTxsWithIncorrectAmount(txStatusListMap.get(TxStatus.APPLIED));
        List<PayableTransactionEntity> failedTxs = txStatusListMap.get(TxStatus.FAILED);

        removeFailedTxs(failedTxs);
        sendTxsToZkCloudWorker(appliedTxs);
        applyReservedTxs(appliedTxs);
    }

    private void sendTxsToZkCloudWorker(List<PayableTransactionEntity> appliedTxs) {
        //todo add client to zkCloudWorker
    }

    private void applyReservedTxs(List<PayableTransactionEntity> pendingTxs) {
        List<PayableTransactionEntity> appliedTxs = pendingTxs.stream()
                .peek(payableTransaction -> payableTransaction.setTxStatus(TxStatus.APPLIED))
                .toList();
        saveLogInfo(appliedTxs, APPLIED);
        payableTransactionRepository.saveAll(appliedTxs);
    }

    private void removeFailedTxs(List<PayableTransactionEntity> failedTxs) {
        saveLogInfo(failedTxs, FAILED);
        domainRepository.deleteAllByTransactionIn(failedTxs);
    }

    private List<PayableTransactionEntity> removeTxsWithIncorrectAmount(List<PayableTransactionEntity> appliedTxs) {
        Map<String, PayableTransactionEntity> mapAppliedTxs = appliedTxs.stream()
                .collect(Collectors.toMap(PayableTransactionEntity::getTxHash, Function.identity()));
        List<DomainEntity> domains = domainRepository.findAllByTransactionIn(appliedTxs);
        for (DomainEntity domain : domains) {
            String txHash = domain.getTransaction().getTxHash();
            PayableTransactionEntity payableTransactionEntity = mapAppliedTxs.get(txHash);
            if (payableTransactionEntity.getTxAmount() < domain.getAmount()) {
                mapAppliedTxs.remove(txHash);
            }
        }

        appliedTxs.removeAll(mapAppliedTxs.values());
        saveLogInfo(appliedTxs, INCORRECT_AMOUNT);
        domainRepository.deleteAllByTransactionIn(appliedTxs);
        return List.copyOf(mapAppliedTxs.values());
    }

    private void saveLogInfo(List<PayableTransactionEntity> appliedTxs, LogInfoStatus status) {
        List<LogInfoEntity> failedDomains = domainRepository.findAllByTransactionIn(appliedTxs)
                .stream()
                .map(domainEntity -> buildLogInfoEntity(domainEntity, status))
                .toList();
        logInfoService.saveAllLogInfos(failedDomains);
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
            PayableTransactionEntity payableTransactionEntity = mapPayableTransactions.get(txProjection.getTxHash());
            if (TxStatus.APPLIED.name().equals(txProjection.getStatus().toUpperCase())) {
                payableTransactionEntity.setTxAmount(txProjection.getAmount());
                applied.add(payableTransactionEntity);
            }
            if (TxStatus.FAILED.name().equals(txProjection.getStatus().toUpperCase())) {
                failed.add(payableTransactionEntity);
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

    private LogInfoEntity buildLogInfoEntity(DomainEntity domainEntity, LogInfoStatus status) {
        return LogInfoEntity.builder()
                .logInfoStatus(status)
                .txHash(domainEntity.getTransaction().getTxHash())
                .domainName(domainEntity.getDomainName())
                .amount(domainEntity.getAmount())
                .ownerAddress(domainEntity.getOwnerAddress())
                .build();
    }
}
