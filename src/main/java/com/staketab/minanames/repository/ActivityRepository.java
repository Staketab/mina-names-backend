package com.staketab.minanames.repository;

import com.staketab.minanames.entity.ActivityEntity;
import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.repository.projection.ActivityProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, String> {

    @Query(nativeQuery = true,
            value = """
                    select a.id as id,
                           a.details as details,
                           a.domain_name as domainName,
                           a.owner_address as ownerAddress,
                           a.timestamp as timestamp,
                           a.tx_hash as txHash,
                           a.status as status,
                           d.id as domainId
                      from activity a left join domains d on d.name = a.domain_name
                      where a.owner_address = :address and a.is_show is true""")
    Page<ActivityProjection> findAllActivities(Pageable buildPageable, String address);

    @Query(nativeQuery = true,
            value = """
                    select a.id as id,
                           a.details as details,
                           a.domain_name as domainName,
                           a.owner_address as ownerAddress,
                           a.timestamp as timestamp,
                           a.tx_hash as txHash,
                           a.status as status,
                           d.id as domainId
                      from activity a left join domains d on d.name = a.domain_name
                      where a.domain_name = :domainName and a.is_show is true""")
    Page<ActivityProjection> findAllActivitiesByDomainName(Pageable buildPageable, String domainName);
}
