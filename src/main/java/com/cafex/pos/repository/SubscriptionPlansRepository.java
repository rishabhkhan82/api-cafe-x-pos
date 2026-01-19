package com.cafex.pos.repository;

import com.cafex.pos.entity.SubscriptionPlans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPlansRepository extends JpaRepository<SubscriptionPlans, Long>, JpaSpecificationExecutor<SubscriptionPlans> {
}