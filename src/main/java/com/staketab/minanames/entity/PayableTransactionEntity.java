package com.staketab.minanames.entity;

import com.staketab.minanames.entity.dto.TxStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payable_transactions")
public class PayableTransactionEntity {

    @Id
    @Column(name = "hash", columnDefinition = "TEXT")
    private String txHash;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TxStatus txStatus;

    @OneToMany(mappedBy="transaction", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<DomainEntity> domains;

    @Column(name = "amount")
    private Long txAmount;
}
