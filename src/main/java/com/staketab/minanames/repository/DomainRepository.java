package com.staketab.minanames.repository;

import com.staketab.minanames.entity.DomainEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

    Optional<DomainEntity> findDomainEntityByDomainName(String domainName);
}
