package com.staketab.minanames.repository;

import com.staketab.minanames.entity.DomainEntity;
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
                        where owner_address = :accountAddress and (:searchStr is null or name = :searchStr)""")
    Page<DomainEntity> findAllDomainsByAccount(String searchStr, String accountAddress, Pageable buildPageable);

    @Query(nativeQuery = true,
            value = """
                    SELECT *
                    FROM domains
                    where reservation_timestamp < :reservationTimestamp
                    and status != 'ACTIVE'
                                            """)
    List<DomainEntity> findAllByReservationTimestampLessThan(Long reservationTimestamp);

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

    Optional<DomainEntity> findDomainEntityByDomainName(String domainName);

    @Modifying
    @Transactional
    void deleteAllByTransactionIn(Collection<PayableTransactionEntity> transaction);

    List<DomainEntity> findAllByTransactionIn(Collection<PayableTransactionEntity> transaction);

    List<DomainEntity> findAllByOwnerAddressAndDomainNameIn(String ownerAddress, Collection<String> domainName);
}
