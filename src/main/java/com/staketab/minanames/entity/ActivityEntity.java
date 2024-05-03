package com.staketab.minanames.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "activity")
public class ActivityEntity {

    @Id
    @UuidGenerator
    private String id;

    @Column(name = "status", columnDefinition = "TEXT")
    private String status;

    @Column(name = "owner_address", columnDefinition = "TEXT")
    private String ownerAddress;

    @Column(name = "tx_hash", columnDefinition = "TEXT")
    private String txHash;

    @Column(name = "domain_name", columnDefinition = "TEXT")
    private String domainName;

    private Long amount;

    @CreationTimestamp
    private LocalDateTime date;
}
