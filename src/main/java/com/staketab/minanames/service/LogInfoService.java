package com.staketab.minanames.service;

import com.staketab.minanames.entity.LogInfoEntity;

import java.util.List;

public interface LogInfoService {

    void saveLogInfo(LogInfoEntity logInfoEntity);

    void saveAllLogInfos(List<LogInfoEntity> logInfoEntities);
}
