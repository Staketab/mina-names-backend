package com.staketab.minanames.service.impl;

import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.entity.ActivityEntity;
import com.staketab.minanames.entity.ActivityStatus;
import com.staketab.minanames.repository.ActivityRepository;
import com.staketab.minanames.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    @Override
    public void saveActivity(DomainEntity domainEntity, ActivityStatus status) {
        activityRepository.save(buildActivityEntity(domainEntity, status));
    }

    @Override
    public void saveAllActivities(List<DomainEntity> domainEntities, ActivityStatus status) {
        List<ActivityEntity> activityEntities = domainEntities
                .stream()
                .map(domainEntity -> buildActivityEntity(domainEntity, status))
                .toList();
        activityRepository.saveAll(activityEntities);
    }

    private ActivityEntity buildActivityEntity(DomainEntity domainEntity, ActivityStatus status) {
        return ActivityEntity.builder()
                .status(status.name())
                .txHash(domainEntity.getTransaction().getTxHash())
                .domainName(domainEntity.getDomainName())
                .amount(domainEntity.getAmount())
                .ownerAddress(domainEntity.getOwnerAddress())
                .build();
    }
}
