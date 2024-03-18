package com.staketab.minanames.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.staketab.minanames.entity.dto.DomainStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "domains")
public class DomainEntity {

    @Id
    @UuidGenerator
    private String id;
    @Column(name = "owner_address", columnDefinition = "TEXT")
    private String ownerAddress;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="transaction", nullable=false)
    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="txHash")
    @JsonIdentityReference(alwaysAsId=true)
    private PayableTransactionEntity transaction;

    @Column(name = "name", columnDefinition = "TEXT")
    private String domainName;
    @Column(name = "img", columnDefinition = "TEXT")
    private String domainImg;

    private Double amount;

    @Column(name = "reservation_timestamp")
    private Long reservationTimestamp;
    @Column(name = "expiration_time_years")
    private Integer expirationTime;  // expirationTime  TODO: (need to think about expirationTimestamp)
    @Column(name = "start_timestamp")
    private Long startTimestamp;  // time when tx become applied

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DomainStatus domainStatus;

    @Column(name = "is_send_to_cloud_worker")
    private Boolean isSendToCloudWorker;
    @Column(name = "is_default")
    private Boolean isDefault;
}
