package com.staketab.minanames.repository;

import com.staketab.minanames.entity.ActivityEntity;
import com.staketab.minanames.entity.DomainEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, String> {

    @Query(nativeQuery = true,
            value = """
                    select *
                      from activity
                      where owner_address = :address and is_show is true""")
    Page<ActivityEntity> findAllActivities(Pageable buildPageable, String address);

    @Query(nativeQuery = true,
            value = """
                    select *
                      from activity
                      where domain_name = :domainName and is_show is true""")
    Page<ActivityEntity> findAllActivitiesByDomainName(Pageable buildPageable, String domainName);
}
