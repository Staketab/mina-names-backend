package com.staketab.minanames.repository;

import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.entity.DomainStatus;
import com.staketab.minanames.entity.PayableTransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Repository
public interface DomainRepository extends JpaRepository<DomainEntity, String> {

    @Query(nativeQuery = true,
            value = """
                    select *
                      from domains
                      where (:searchStr is null or name = :searchStr)""")
    Page<DomainEntity> findAllDomains(String searchStr, Pageable buildPageable);

    @Query(nativeQuery = true,
            value = """
                    select *
                      from domains
                      where status = 'ACTIVE'
                      """)
    Page<DomainEntity> findAllDomains(Pageable buildPageable);

    @Query(nativeQuery = true,
            value = """
                    select *
                    from domains
                    where owner_address = :accountAddress
                      and (:searchStr is null or name = :searchStr)
                      and (:domainStatus is null or status = :domainStatus)
                    """)
    Page<DomainEntity> findAllDomainsByAccount(String searchStr, String accountAddress, String domainStatus, Pageable buildPageable);

    @Query(nativeQuery = true,
            value = """
                    SELECT *
                    FROM domains
                    where reservation_timestamp < :reservationTimestamp
                    and status = :status
                    """)
    List<DomainEntity> findAllByReservationTimestampLessThan(Long reservationTimestamp, String status);

    @Query(nativeQuery = true,
            value = """
                    SELECT *
                    FROM domains
                    where owner_address = (select owner_address from domains where id = :id)
                    and status = 'RESERVED'
                    """)
    List<DomainEntity> findAllCartsReservedDomains(String id);

    @Modifying
    @Query(nativeQuery = true,
            value = """
                    UPDATE domains
                    SET is_default = CASE
                        WHEN id = :id THEN true
                        ELSE false
                        END
                    WHERE owner_address = (select owner_address from domains where id = :id)
                    """)
    int setDefaultDomain(String id);

    @Modifying
    @Query(nativeQuery = true,
            value = """
                    UPDATE domains
                    SET is_default = false
                    WHERE id = :id
                    """)
    int removeDefaultDomain(String id);

    Optional<DomainEntity> findDomainEntityByDomainName(String domainName);

    Optional<DomainEntity> findDomainEntityByDomainNameAndOwnerAddress(String domainName, String ownerAddress);

    @Modifying
    @Transactional
    void deleteAllByTransactionIn(Collection<PayableTransactionEntity> transaction);

    List<DomainEntity> findAllByTransactionIn(Collection<PayableTransactionEntity> transaction);

    List<DomainEntity> findAllByOwnerAddressAndDomainNameIn(String ownerAddress, Collection<String> domainName);

    List<DomainEntity> findAllByIsSendToCloudWorkerTrueAndDomainStatus(String domainStatus);

    @Query(nativeQuery = true,
    value = """
            select block_number
            from domains
            order by block_number desc
            limit 1     
            """)
    Long findTopBlockNumber();
}
