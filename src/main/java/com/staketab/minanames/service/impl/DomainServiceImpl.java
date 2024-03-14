package com.staketab.minanames.service.impl;

import com.staketab.minanames.dto.DomainReservationDTO;
import com.staketab.minanames.dto.DomainUpdateDTO;
import com.staketab.minanames.dto.request.BaseRequest;
import com.staketab.minanames.dto.request.SearchParams;
import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.entity.dto.DomainStatus;
import com.staketab.minanames.repository.DomainRepository;
import com.staketab.minanames.service.abstraction.DomainService;
import com.staketab.minanames.service.abstraction.TxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DomainServiceImpl implements DomainService {

    private final DomainRepository domainRepository;
    private final TxService txService;

    @Override
    public Page<DomainEntity> findAllByPageable(BaseRequest request, SearchParams searchParams) {
        return domainRepository.findAllDomains(searchParams.getSearchStr(), request.buildPageable());
    }

    @Override
    public Page<DomainEntity> findAllByAccountPageable(BaseRequest request, String accountAddress, SearchParams searchParams) {
        return domainRepository.findAllDomainsByAccount(searchParams.getSearchStr(), accountAddress, request.buildPageable());
    }

    @Override
    public DomainEntity create(DomainReservationDTO request) {
        DomainEntity domain = DomainEntity.builder()
                .ownerAddress(request.getOwnerAddress())
                .transaction(txService.getOrCreate(request.getTxHash()))
                .domainName(request.getDomainName())
                .amount(request.getAmount())
                .expirationTime(request.getExpirationTime())
                .reservationTimestamp(System.currentTimeMillis())
                .domainStatus(DomainStatus.PENDING)
                .isSendToCloudWorker(false)
                .isDefault(false)
                .build();
        return domainRepository.save(domain);
    }

    @Override
    public Optional<DomainEntity> retrieve(String id) {
        return domainRepository.findById(id);
    }

    @Override
    public DomainEntity update(DomainUpdateDTO domainUpdateDTO) {
        return null;
    }

    @Override
    public Boolean isNameReserved(String name) {
        return domainRepository.findDomainEntityByDomainName(name).isPresent();
    }

    @Override
    @Transactional
    public Boolean setDefaultDomain(String id) {
        return domainRepository.setDefaultDomain(id) > 0;
    }
}
