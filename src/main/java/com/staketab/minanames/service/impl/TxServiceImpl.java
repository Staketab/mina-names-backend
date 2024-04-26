package com.staketab.minanames.service.impl;

import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.entity.LogInfoStatus;
import com.staketab.minanames.entity.PayableTransactionEntity;
import com.staketab.minanames.entity.TxStatus;
import com.staketab.minanames.minascan.TransactionRepository;
import com.staketab.minanames.minascan.TxProjection;
import com.staketab.minanames.repository.DomainRepository;
import com.staketab.minanames.repository.PayableTransactionRepository;
import com.staketab.minanames.service.LogInfoService;
import com.staketab.minanames.service.TxService;
import com.staketab.minanames.service.ZkCloudWorkerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.staketab.minanames.entity.LogInfoStatus.APPLIED;
import static com.staketab.minanames.entity.LogInfoStatus.FAILED;
import static com.staketab.minanames.entity.LogInfoStatus.INCORRECT_AMOUNT;

@Slf4j
@Service
@RequiredArgsConstructor
public class TxServiceImpl implements TxService {

    private final LogInfoService logInfoService;
    private final DomainRepository domainRepository;
    private final ZkCloudWorkerService zkCloudWorkerService;
    private final TransactionRepository transactionRepository;
    private final PayableTransactionRepository payableTransactionRepository;

    @Override
    public PayableTransactionEntity getOrCreate(String txHash, int countDomains, TxStatus status) {
        return payableTransactionRepository.findById(txHash).orElseGet(() -> createTx(txHash, countDomains, status));
    }

    @Override
    @Transactional
    public void checkTransactions() {
        List<PayableTransactionEntity> pendingTxs = payableTransactionRepository.findAllByTxStatus(TxStatus.PENDING);
        Map<TxStatus, List<PayableTransactionEntity>> txStatusListMap = generateMapOfFailedAndAppliedTxs(pendingTxs);
        List<PayableTransactionEntity> appliedTxs = txStatusListMap.get(TxStatus.APPLIED);
        List<PayableTransactionEntity> failedTxs = txStatusListMap.get(TxStatus.FAILED);
        List<PayableTransactionEntity> correctAppliedTxs = txsWithoutIncorrectAmount(appliedTxs);

        removeFailedTxs(failedTxs);
        applyReservedTxs(correctAppliedTxs);
        zkCloudWorkerService.sendTxs(correctAppliedTxs);
    }

    @Override
    @Transactional
    public void deleteTxsWithIncorrectAmount() {
        List<PayableTransactionEntity> pendingTxs = payableTransactionRepository.findAllByTxStatus(TxStatus.PENDING);
        List<PayableTransactionEntity> correctAppliedTxs = txsWithIncorrectAmount(pendingTxs);
        pendingTxs.retainAll(correctAppliedTxs);
        saveLogInfo(pendingTxs, INCORRECT_AMOUNT);
        domainRepository.deleteAllByTransactionIn(pendingTxs);
    }

    @Override
    public void deleteTxs(List<String> txHashes) {
        payableTransactionRepository.deleteAllByTxHashIn(txHashes);
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

    private List<PayableTransactionEntity> txsWithoutIncorrectAmount(List<PayableTransactionEntity> appliedTxs) {
        List<PayableTransactionEntity> multipleTxs = appliedTxs.stream()
                .filter(tx -> tx.getCountDomains() > 1)
                .toList();

        Map<String, Long> txHashAndAmountMap = calculateTotalAmountByTransaction(domainRepository.findAllByTransactionIn(multipleTxs));
        appliedTxs.removeIf(tx -> {
            Long amount = txHashAndAmountMap.get(tx.getTxHash());
            return amount != null && tx.getTxAmount() < amount;
        });

        Map<String, PayableTransactionEntity> appliedTxsMap = appliedTxs.stream()
                .collect(Collectors.toMap(PayableTransactionEntity::getTxHash, Function.identity()));

        domainRepository.findAllByTransactionIn(appliedTxs).forEach(domain -> {
            String txHash = domain.getTransaction().getTxHash();
            PayableTransactionEntity tx = appliedTxsMap.get(txHash);
            if (tx != null && tx.getTxAmount() < domain.getAmount()) {
                appliedTxsMap.remove(txHash);
            }
        });

        appliedTxs.retainAll(appliedTxsMap.values());
        return List.copyOf(appliedTxsMap.values());
    }

    private List<PayableTransactionEntity> txsWithIncorrectAmount(List<PayableTransactionEntity> appliedTxs) {
        List<PayableTransactionEntity> multipleTxs = appliedTxs.stream()
                .filter(tx -> tx.getCountDomains() > 1)
                .toList();

        Map<String, Long> txHashAndAmountMap = calculateTotalAmountByTransaction(domainRepository.findAllByTransactionIn(multipleTxs));
        appliedTxs.removeIf(tx -> {
            Long amount = txHashAndAmountMap.get(tx.getTxHash());
            return amount != null && tx.getTxAmount() >= amount;
        });

        Map<String, PayableTransactionEntity> appliedTxsMap = appliedTxs.stream()
                .collect(Collectors.toMap(PayableTransactionEntity::getTxHash, Function.identity()));

        domainRepository.findAllByTransactionIn(appliedTxs).forEach(domain -> {
            String txHash = domain.getTransaction().getTxHash();
            PayableTransactionEntity tx = appliedTxsMap.get(txHash);
            if (tx != null && tx.getTxAmount() != null && tx.getTxAmount() >= domain.getAmount()) {
                appliedTxsMap.remove(txHash);
            }
        });

        appliedTxs.retainAll(appliedTxsMap.values());
        return appliedTxs;
    }

    public static Map<String, Long> calculateTotalAmountByTransaction(List<DomainEntity> domainList) {
        return domainList.stream()
                .collect(Collectors.groupingBy(
                        domain -> domain.getTransaction().getTxHash(),
                        Collectors.summingLong(DomainEntity::getAmount)
                ));
    }

    private void saveLogInfo(List<PayableTransactionEntity> txs, LogInfoStatus status) {
        List<DomainEntity> domainEntities = domainRepository.findAllByTransactionIn(txs);
        logInfoService.saveAllLogInfos(domainEntities, status);
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

    private PayableTransactionEntity createTx(String txHash, Integer countDomains, TxStatus status) {
        return payableTransactionRepository.save(PayableTransactionEntity.builder()
                .txHash(txHash)
                .txStatus(status)
                .countDomains(countDomains)
                .build());
    }
}
