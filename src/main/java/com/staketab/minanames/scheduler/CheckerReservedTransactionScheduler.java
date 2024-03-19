package com.staketab.minanames.scheduler;

import com.staketab.minanames.service.TxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "scheduled.checker-tx-reserve.enabled", havingValue = "true")
public class CheckerReservedTransactionScheduler {

    private final TxService txService;

    @Scheduled(fixedDelayString = "${scheduled.checker-tx-reserve.upload-mills}")
    public void checkReservedTxs() {
        txService.checkTransactions();
    }
}
