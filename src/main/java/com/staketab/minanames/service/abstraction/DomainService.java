package com.staketab.minanames.service.abstraction;

import com.staketab.minanames.dto.DomainReservationDTO;
import com.staketab.minanames.dto.DomainUpdateDTO;
import com.staketab.minanames.entity.DomainEntity;

import java.util.Optional;

public interface DomainService {
    DomainEntity create(DomainReservationDTO domainRequest);
    Optional<DomainEntity> retrieve(String id);
    DomainEntity update(DomainUpdateDTO domainUpdateDTO);
}
