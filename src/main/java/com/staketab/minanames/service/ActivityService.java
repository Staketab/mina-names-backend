package com.staketab.minanames.service;

import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.entity.ActivityStatus;

import java.util.List;

public interface ActivityService {

    void saveActivity(DomainEntity domainEntity, ActivityStatus status);

    void saveAllActivities(List<DomainEntity> domainEntities, ActivityStatus status);
}
