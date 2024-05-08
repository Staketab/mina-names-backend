package com.staketab.minanames.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "activity", indexes = {
        @Index(name = "timestamp_index", columnList = "timestamp"),
        @Index(name = "domain_name_index", columnList = "domain_name"),
        @Index(name = "owner_address_index", columnList = "owner_address")
})
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

    @Column(name = "ipfs", columnDefinition = "TEXT")
    private String ipfs;

    private Long amount;

    @Column(name = "details")
    private String details;

    @Column(name = "timestamp")
    private long timestamp;

    @Column(name = "is_show")
    private boolean isShow = false;
}
