package com.staketab.minanames.controller;

import com.staketab.minanames.dto.DomainReservationDTO;
import com.staketab.minanames.dto.DomainUpdateDTO;
import com.staketab.minanames.dto.ReservedDomainDTO;
import com.staketab.minanames.dto.request.BaseRequest;
import com.staketab.minanames.dto.request.SearchParams;
import com.staketab.minanames.dto.request.sort.DomainsSortColumn;
import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.service.abstraction.DomainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/domains")
@RequiredArgsConstructor
@Slf4j
public class DomainController {

    private final DomainService domainService;

    @GetMapping("/")
    @Operation(summary = "getDomains", description = "Get a page of all domains.")
    public Page<DomainEntity> getDomains(@Valid @ParameterObject BaseRequest request,
                                         @RequestParam @Schema(defaultValue = "RESERVATION_TIMESTAMP", allowableValues =
                                                 {"AMOUNT", "STATUS", "RESERVATION_TIMESTAMP", "IS_SEND_TO_CLOUD_WORKER"},
                                                 description = "Select sorting parameter.") DomainsSortColumn sortBy,
                                         @RequestParam(required = false, defaultValue = "")
                                         @Parameter(description = "Domain Name") String searchStr) {
        SearchParams searchParams = new SearchParams(searchStr);
        return domainService.findAllByPageable(request.withSortColumn(sortBy), searchParams);
    }

    @GetMapping("/accounts/{accountAddress}")
    @Operation(summary = "getAccountDomains", description = "Get a page of account's domains.")
    public Page<DomainEntity> getAccountDomains(@Valid @ParameterObject BaseRequest request,
                                                @PathVariable String accountAddress,
                                                @RequestParam @Schema(defaultValue = "RESERVATION_TIMESTAMP", allowableValues =
                                                        {"AMOUNT", "STATUS", "RESERVATION_TIMESTAMP", "IS_SEND_TO_CLOUD_WORKER"},
                                                        description = "Select sorting parameter.") DomainsSortColumn sortBy,
                                                @RequestParam(required = false, defaultValue = "")
                                                @Parameter(description = "Domain Name") String searchStr) {
        SearchParams searchParams = new SearchParams(searchStr);
        return domainService.findAllByAccountPageable(request.withSortColumn(sortBy), accountAddress, searchParams);
    }

    @PostMapping("/save")
    public ResponseEntity<DomainEntity> create(@RequestBody DomainReservationDTO domainRequest) {
        try {
            return ok(domainService.create(domainRequest));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DomainEntity> retrieve(@PathVariable String id) {
        try {
            return ok(domainService.retrieve(id).orElseThrow(NoSuchElementException::new));
        } catch (NoSuchElementException e) {
            return notFound().build();
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<DomainEntity> update(@RequestBody DomainUpdateDTO domainUpdate) {
        try {
            return ok(domainService.update(domainUpdate));
        } catch (NoSuchElementException e) {
            return notFound().build();
        }
    }

    @GetMapping("/{domainName}/reserved")
    public ResponseEntity<ReservedDomainDTO> isDomainNameReserved(@PathVariable String domainName) {
        return ok(domainService.isNameReserved(domainName));
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<Boolean> setDefaultDomain(@PathVariable String id) {
        return ok(domainService.setDefaultDomain(id));
    }
}
