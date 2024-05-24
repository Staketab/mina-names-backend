package com.staketab.minanames.service;

import com.staketab.minanames.dto.ApplyReservedDomainDTO;
import com.staketab.minanames.dto.DomainCartReservationDTO;
import com.staketab.minanames.dto.DomainCartReserveUpdateDTO;
import com.staketab.minanames.dto.DomainDTO;
import com.staketab.minanames.dto.DomainReservationDTO;
import com.staketab.minanames.dto.DomainUpdateDTO;
import com.staketab.minanames.dto.ReservedDomainDTO;
import com.staketab.minanames.dto.SimpleDomainDTO;
import com.staketab.minanames.dto.request.BaseRequest;
import com.staketab.minanames.dto.request.SearchParams;
import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.entity.DomainStatus;
import org.springframework.data.domain.Page;

public interface DomainService {
    Page<DomainEntity> findAllByPageable(BaseRequest request, SearchParams searchParams);

    Page<SimpleDomainDTO> findAllSimpleDomainsByPageable(BaseRequest request);

    Page<DomainEntity> findAllByAccountPageable(BaseRequest request, String accountAddress, SearchParams searchParams,
                                                String domainStatus);

    DomainEntity create(DomainReservationDTO domainRequest);

    DomainEntity reserve(DomainCartReservationDTO domainRequest);

    void updateReserve(DomainCartReserveUpdateDTO domainRequest);

    void applyReservedDomain(ApplyReservedDomainDTO domainRequest);

    void removeReservedDomain(String id);

    DomainDTO retrieve(String id);

    DomainEntity update(DomainUpdateDTO domainUpdateDTO);

    ReservedDomainDTO isNameReserved(String name);

    Boolean setDefaultDomain(String id);

    Boolean removeDefaultDomain(String id);

    void removeReservedDomains();

    void removeCartReservedDomains();
}
