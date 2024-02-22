package com.staketab.minanames.controller;

import com.staketab.minanames.dto.DomainRequestDTO;
import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.service.abstraction.DomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/domains")
@RequiredArgsConstructor
@Slf4j
public class DomainController {

    private final DomainService domainService;

    @PostMapping("/save")
    public ResponseEntity<DomainEntity> create(@RequestBody DomainRequestDTO domainRequest) {
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
            return ResponseEntity.notFound().build();
        }
    }
}
