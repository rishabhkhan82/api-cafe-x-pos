package com.cafex.pos.repository;

import com.cafex.pos.entity.BillingPeriodMonthsMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BillingPeriodMonthsMasterRepository extends JpaRepository<BillingPeriodMonthsMaster, Long>, JpaSpecificationExecutor<BillingPeriodMonthsMaster> {
    boolean existsByKey(String key);
    boolean existsByName(String name);
}
