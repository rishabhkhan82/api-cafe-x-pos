package com.cafex.pos.repository;

import com.cafex.pos.entity.TrialDaysMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TrialDaysMasterRepository extends JpaRepository<TrialDaysMaster, Long>, JpaSpecificationExecutor<TrialDaysMaster> {
    boolean existsByKey(String key);
    boolean existsByName(String name);
}
