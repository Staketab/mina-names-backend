package com.staketab.minanames.service.abstraction;

import com.staketab.minanames.dto.DomainReservationDTO;
import com.staketab.minanames.dto.DomainUpdateDTO;
import com.staketab.minanames.dto.request.BaseRequest;
import com.staketab.minanames.dto.request.SearchParams;
import com.staketab.minanames.entity.DomainEntity;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface DomainService {
    Page<DomainEntity> findAllByPageable(BaseRequest request, SearchParams searchParams);
    Page<DomainEntity> findAllByAccountPageable(BaseRequest request, String accountAddress, SearchParams searchParams);
    DomainEntity create(DomainReservationDTO domainRequest);
    Optional<DomainEntity> retrieve(String id);
    DomainEntity update(DomainUpdateDTO domainUpdateDTO);
}
