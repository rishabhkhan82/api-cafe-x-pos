package com.cafex.pos.repository;

import com.cafex.pos.entity.SetupFeesMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SetupFeesMasterRepository extends JpaRepository<SetupFeesMaster, Long>, JpaSpecificationExecutor<SetupFeesMaster> {
    boolean existsByKey(String key);
    boolean existsByName(String name);
}
