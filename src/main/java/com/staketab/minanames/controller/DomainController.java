package com.staketab.minanames.controller;

import com.staketab.minanames.dto.DomainReservationDTO;
import com.staketab.minanames.dto.DomainUpdateDTO;
import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.service.abstraction.DomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/domains")
@RequiredArgsConstructor
@Slf4j
public class DomainController {

    private final DomainService domainService;

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
}
