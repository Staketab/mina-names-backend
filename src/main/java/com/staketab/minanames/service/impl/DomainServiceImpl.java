package com.staketab.minanames.service.impl;

import com.staketab.minanames.dto.DomainRequestDTO;
import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.service.abstraction.DomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DomainServiceImpl implements DomainService {

    @Override
    public DomainEntity create(DomainRequestDTO domainRequest) {
        return null;
    }

    @Override
    public Optional<DomainEntity> retrieve(String id) {
        return Optional.empty();
    }
}
