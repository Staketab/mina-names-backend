package com.staketab.minanames.service.abstraction;

import com.staketab.minanames.dto.DomainRequestDTO;
import com.staketab.minanames.entity.DomainEntity;

import java.util.Optional;

public interface DomainService {
    DomainEntity create(DomainRequestDTO domainRequest);
    Optional<DomainEntity> retrieve(String id);
}
