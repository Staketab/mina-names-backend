package com.staketab.minanames.service.impl;

import com.staketab.minanames.dto.ActivityDTO;
import com.staketab.minanames.dto.request.BaseRequest;
import com.staketab.minanames.entity.ActivityEntity;
import com.staketab.minanames.entity.ActivityStatus;
import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.repository.ActivityRepository;
import com.staketab.minanames.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.staketab.minanames.entity.ActivityStatus.SET_ACTIVE_STATUS_FOR_DOMAIN;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    @Override
    public Page<ActivityDTO> findAllByPageable(BaseRequest baseRequest, String address) {
        return activityRepository.findAllActivities(baseRequest.buildPageable(), address).map(this::buildActivityDTO);
    }

    @Override
    public Page<ActivityDTO> findAllByDomainNameAndPageable(BaseRequest baseRequest, String domainName) {
        return activityRepository.findAllActivitiesByDomainName(baseRequest.buildPageable(), domainName)
                .map(this::buildActivityDTO);
    }

    @Override
    public void saveActivity(DomainEntity domainEntity, ActivityStatus status, String details) {
        activityRepository.save(buildActivityEntity(domainEntity, status, details));
    }

    @Override
    public void saveAllActivities(List<DomainEntity> domainEntities, ActivityStatus status, String details) {
        List<ActivityEntity> activityEntities = domainEntities
                .stream()
                .map(domainEntity -> buildActivityEntity(domainEntity, status, details))
                .toList();
        activityRepository.saveAll(activityEntities);
    }

    private ActivityEntity buildActivityEntity(DomainEntity domainEntity, ActivityStatus status, String details) {
        boolean equals = SET_ACTIVE_STATUS_FOR_DOMAIN.equals(status);
        return ActivityEntity.builder()
                .status(status.name())
                .txHash(equals ? domainEntity.getTransaction().getTxHash() : null)
                .domainName(domainEntity.getDomainName())
                .details(details)
                .ownerAddress(domainEntity.getOwnerAddress())
                .timestamp(System.currentTimeMillis())
                .ipfs(domainEntity.getIpfs())
                .amount(equals ? domainEntity.getAmount() : null)
                .isShow(status.isShow())
                .build();
    }

    private ActivityDTO buildActivityDTO(ActivityEntity activityEntity) {
        return ActivityDTO.builder()
                .id(activityEntity.getId())
                .activity(ActivityStatus.valueOf(activityEntity.getStatus()).getMessage())
                .transaction(activityEntity.getTxHash())
                .details(activityEntity.getDetails())
                .timestamp(activityEntity.getTimestamp())
                .ownerAddress(activityEntity.getOwnerAddress())
                .domainName(activityEntity.getDomainName())
                .build();
    }
}
