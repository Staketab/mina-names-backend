package com.staketab.minanames.service;

import com.staketab.minanames.dto.ActivityDTO;
import com.staketab.minanames.dto.request.BaseRequest;
import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.entity.ActivityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityService {

    Page<ActivityDTO> findAllByPageable(BaseRequest baseRequest, String address);

    void saveActivity(DomainEntity domainEntity, ActivityStatus status, String details);

    void saveAllActivities(List<DomainEntity> domainEntities, ActivityStatus status, String details);
}
