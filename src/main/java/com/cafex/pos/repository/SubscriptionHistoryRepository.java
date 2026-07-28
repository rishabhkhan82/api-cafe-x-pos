package com.cafex.pos.repository;

import com.cafex.pos.entity.SubscriptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Long>, JpaSpecificationExecutor<SubscriptionHistory> {
}