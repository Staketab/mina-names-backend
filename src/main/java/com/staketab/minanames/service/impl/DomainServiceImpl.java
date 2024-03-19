package com.staketab.minanames.service.impl;

import com.staketab.minanames.dto.DomainReservationDTO;
import com.staketab.minanames.dto.DomainUpdateDTO;
import com.staketab.minanames.dto.ReservedDomainDTO;
import com.staketab.minanames.dto.request.BaseRequest;
import com.staketab.minanames.dto.request.SearchParams;
import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.entity.dto.DomainDTO;
import com.staketab.minanames.entity.dto.DomainStatus;
import com.staketab.minanames.exception.NotFoundException;
import com.staketab.minanames.repository.DomainRepository;
import com.staketab.minanames.service.DomainService;
import com.staketab.minanames.service.TxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.staketab.minanames.utils.Constants.DEFAULT_DENOMINATION;
import static com.staketab.minanames.utils.Constants.MINA_DENOMINATION;

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
                .amount(Math.round(request.getAmount() * MINA_DENOMINATION))
                .expirationTime(request.getExpirationTime())
                .reservationTimestamp(System.currentTimeMillis())
                .domainStatus(DomainStatus.PENDING)
                .isSendToCloudWorker(false)
                .isDefault(false)
                .build();
        return domainRepository.save(domain);
    }

    @Override
    public DomainDTO retrieve(String id) {
        return domainRepository.findById(id)
                .map(this::buildDomainDTO)
                .orElseThrow(() -> new NotFoundException(String.format("Domain doesn't found by id: %s", id)));
    }

    @Override
    public DomainEntity update(DomainUpdateDTO domainUpdateDTO) {
        Optional<DomainEntity> optionalDomainEntity = domainRepository.findById(domainUpdateDTO.getId());
        if (optionalDomainEntity.isEmpty()) {
            throw new NotFoundException(String.format("Domain doesn't found by id: %s", domainUpdateDTO.getId()));
        }
        DomainEntity domainEntity = optionalDomainEntity.get();
        domainEntity.setDomainImg(domainUpdateDTO.getImg());
        return domainRepository.save(domainEntity);
    }

    @Override
    public ReservedDomainDTO isNameReserved(String name) {
        return domainRepository.findDomainEntityByDomainName(name)
                .map(this::mapToReservedDomainDTO)
                .orElseGet(ReservedDomainDTO::new);
    }

    @Override
    @Transactional
    public Boolean setDefaultDomain(String id) {
        return domainRepository.setDefaultDomain(id) > 0;
    }

    @Override
    public void removeReservedDomains() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(1);
        long currentTimestamp = Timestamp.valueOf(localDateTime).getTime();
        domainRepository.deleteAllByReservationTimestampLessThan(currentTimestamp);
    }

    private ReservedDomainDTO mapToReservedDomainDTO(DomainEntity domainEntity) {
        return ReservedDomainDTO.builder()
                .id(domainEntity.getId())
                .build();
    }

    private DomainDTO buildDomainDTO(DomainEntity domainEntity) {
        return DomainDTO.builder()
                .id(domainEntity.getId())
                .domainImg(domainEntity.getDomainImg())
                .amount(BigDecimal.valueOf(domainEntity.getAmount()).divide(DEFAULT_DENOMINATION, RoundingMode.HALF_UP))
                .startTimestamp(domainEntity.getStartTimestamp())
                .domainStatus(domainEntity.getDomainStatus())
                .expirationTime(domainEntity.getExpirationTime())
                .isDefault(domainEntity.getIsDefault())
                .isSendToCloudWorker(domainEntity.getIsSendToCloudWorker())
                .ownerAddress(domainEntity.getOwnerAddress())
                .reservationTimestamp(domainEntity.getReservationTimestamp())
                .transaction(domainEntity.getTransaction().getTxHash())
                .build();
    }
}
