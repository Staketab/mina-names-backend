package com.staketab.minanames.service.impl;

import com.staketab.minanames.dto.DomainReservationDTO;
import com.staketab.minanames.dto.DomainUpdateDTO;
import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.entity.PayableTransactionEntity;
import com.staketab.minanames.entity.dto.DomainStatus;
import com.staketab.minanames.entity.dto.TxStatus;
import com.staketab.minanames.repository.DomainRepository;
import com.staketab.minanames.service.abstraction.DomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DomainServiceImpl implements DomainService {

    private final DomainRepository domainRepository;

    @Override
    public DomainEntity create(DomainReservationDTO request) {
        PayableTransactionEntity tx = PayableTransactionEntity.builder()
                .txHash(request.getTxHash())
                .reservationTimestamp(System.currentTimeMillis())
                .txStatus(TxStatus.PENDING)
                .build();

        DomainEntity domain = DomainEntity.builder()
                .ownerAddress(request.getOwnerAddress())
                .transaction(tx)
                .domainName(request.getDomainName())
                .amount(request.getAmount())
                .expirationTime(request.getExpirationTime())
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
}
