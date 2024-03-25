package com.staketab.minanames.repository;

import com.staketab.minanames.entity.LogInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogInfoRepository extends JpaRepository<LogInfoEntity, String> {

}
