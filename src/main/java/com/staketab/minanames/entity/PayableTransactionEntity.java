package com.staketab.minanames.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payable_transactions")
public class PayableTransactionEntity {

    @Id
    @UuidGenerator
    private String id;

    @Column(name = "hash", columnDefinition = "TEXT")
    private String txHash;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TxStatus txStatus;

    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<DomainEntity> domains;

    @Column(name = "amount")
    private Long txAmount;

    @Column(name = "count_domains")
    private Integer countDomains;
}
