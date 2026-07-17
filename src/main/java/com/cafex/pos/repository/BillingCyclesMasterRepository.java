package com.cafex.pos.repository;

import com.cafex.pos.entity.BillingCyclesMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BillingCyclesMasterRepository extends JpaRepository<BillingCyclesMaster, Long>, JpaSpecificationExecutor<BillingCyclesMaster> {
    boolean existsByKey(String key);
    boolean existsByName(String name);
}
