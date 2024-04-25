package com.staketab.minanames.service;

import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.entity.LogInfoStatus;

import java.util.List;

public interface LogInfoService {

    void saveLogInfo(DomainEntity domainEntity, LogInfoStatus status);

    void saveAllLogInfos(List<DomainEntity> domainEntities, LogInfoStatus status);
}
