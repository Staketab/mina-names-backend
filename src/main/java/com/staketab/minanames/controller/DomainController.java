package com.staketab.minanames.controller;

import com.staketab.minanames.dto.ApplyReservedDomainDTO;
import com.staketab.minanames.dto.DomainDTO;
import com.staketab.minanames.dto.DomainReservationDTO;
import com.staketab.minanames.dto.DomainTxSaveDTO;
import com.staketab.minanames.dto.DomainUpdateDTO;
import com.staketab.minanames.dto.ReservedDomainDTO;
import com.staketab.minanames.dto.request.BaseRequest;
import com.staketab.minanames.dto.request.CartDomainTxSaveDTO;
import com.staketab.minanames.dto.request.DomainCartReservationDTO;
import com.staketab.minanames.dto.request.SearchParams;
import com.staketab.minanames.dto.request.sort.DomainsSortColumn;
import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.service.DomainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        return ok(domainService.create(domainRequest));
    }

    @PostMapping("/tx/save")
    public void saveTx(@RequestBody DomainTxSaveDTO domainRequest) {
        domainService.saveTx(domainRequest);
    }

    @PostMapping("/cart/tx/save")
    public void saveCartTx(@RequestBody CartDomainTxSaveDTO domainRequest) {
        domainService.saveCartTx(domainRequest);
    }

    @PostMapping("/reserve")
    public ResponseEntity<DomainEntity> reserve(@RequestBody DomainCartReservationDTO domainRequest) {
        return ok(domainService.reserve(domainRequest));
    }

    @PostMapping("/reserve/apply")
    public void applyReservedDomains(@RequestBody ApplyReservedDomainDTO domainRequest) {
        domainService.applyReservedDomain(domainRequest);
    }

    @DeleteMapping("/reserve/{id}")
    public void deleteReservedDomain(@PathVariable String id) {
        domainService.removeReservedDomain(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DomainDTO> retrieve(@PathVariable String id) {
        return ok(domainService.retrieve(id));
    }

    @PutMapping("/edit")
    public ResponseEntity<DomainEntity> update(@RequestBody DomainUpdateDTO domainUpdate) {
        return ok(domainService.update(domainUpdate));
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
