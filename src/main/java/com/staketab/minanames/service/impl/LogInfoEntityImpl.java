package com.staketab.minanames.service.impl;

import com.staketab.minanames.entity.LogInfoEntity;
import com.staketab.minanames.repository.LogInfoRepository;
import com.staketab.minanames.service.LogInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogInfoEntityImpl implements LogInfoService {

    private final LogInfoRepository logInfoRepository;

    @Override
    public void saveLogInfo(LogInfoEntity logInfoEntity) {
        logInfoRepository.save(logInfoEntity);
    }

    @Override
    public void saveAllLogInfos(List<LogInfoEntity> logInfoEntities) {
        logInfoRepository.saveAll(logInfoEntities);
    }
}
