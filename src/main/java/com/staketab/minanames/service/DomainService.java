package com.staketab.minanames.service;

import com.staketab.minanames.dto.DomainReservationDTO;
import com.staketab.minanames.dto.DomainUpdateDTO;
import com.staketab.minanames.dto.ReservedDomainDTO;
import com.staketab.minanames.dto.request.BaseRequest;
import com.staketab.minanames.dto.request.SearchParams;
import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.entity.dto.DomainDTO;
import org.springframework.data.domain.Page;

public interface DomainService {
    Page<DomainEntity> findAllByPageable(BaseRequest request, SearchParams searchParams);

    Page<DomainEntity> findAllByAccountPageable(BaseRequest request, String accountAddress, SearchParams searchParams);

    DomainEntity create(DomainReservationDTO domainRequest);

    DomainDTO retrieve(String id);

    DomainEntity update(DomainUpdateDTO domainUpdateDTO);

    ReservedDomainDTO isNameReserved(String name);

    Boolean setDefaultDomain(String id);

    void removeReservedDomains();
}
