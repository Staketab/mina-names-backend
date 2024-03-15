package com.staketab.minanames.scheduler;

import com.staketab.minanames.service.abstraction.DomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "scheduled.domain.enabled", havingValue = "true")
public class DomainScheduler {

    private final DomainService domainService;

    @Scheduled(fixedDelayString = "${scheduled.domain.upload-mills}")
    public void removeReservedDomains() {
         domainService.removeReservedDomains();
    }
}
