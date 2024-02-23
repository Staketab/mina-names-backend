package com.staketab.minanames.service.impl;

import com.staketab.minanames.entity.PayableTransactionEntity;
import com.staketab.minanames.entity.dto.TxStatus;
import com.staketab.minanames.repository.PayableTransactionRepository;
import com.staketab.minanames.service.abstraction.TxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TxServiceImpl implements TxService {

    private final PayableTransactionRepository payableTransactionRepository;

    @Override
    public PayableTransactionEntity getOrCreate(String txHash) {
        return payableTransactionRepository.findById(txHash).orElseGet(() -> createTx(txHash));
    }

    private PayableTransactionEntity createTx(String txHash) {
        return payableTransactionRepository.save(PayableTransactionEntity.builder()
                .txHash(txHash)
                .txStatus(TxStatus.PENDING)
                .build());
    }
}
