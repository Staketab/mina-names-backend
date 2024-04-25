package com.staketab.minanames.service.impl;

import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.entity.LogInfoEntity;
import com.staketab.minanames.entity.LogInfoStatus;
import com.staketab.minanames.repository.LogInfoRepository;
import com.staketab.minanames.service.LogInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogInfoServiceImpl implements LogInfoService {

    private final LogInfoRepository logInfoRepository;

    @Override
    public void saveLogInfo(DomainEntity domainEntity, LogInfoStatus status) {
        logInfoRepository.save(buildLogInfoEntity(domainEntity, status));
    }

    @Override
    public void saveAllLogInfos(List<DomainEntity> domainEntities, LogInfoStatus status) {
        List<LogInfoEntity> logInfoEntities = domainEntities
                .stream()
                .map(domainEntity -> buildLogInfoEntity(domainEntity, status))
                .toList();
        logInfoRepository.saveAll(logInfoEntities);
    }

    private LogInfoEntity buildLogInfoEntity(DomainEntity domainEntity, LogInfoStatus status) {
        return LogInfoEntity.builder()
                .logInfoStatus(status.name())
                .txHash(domainEntity.getTransaction().getTxHash())
                .domainName(domainEntity.getDomainName())
                .amount(domainEntity.getAmount())
                .ownerAddress(domainEntity.getOwnerAddress())
                .build();
    }
}
