package com.cafex.pos.repository;

import com.cafex.pos.entity.RestaurantSubscriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface RestaurantSubscriptionRepository extends JpaRepository<RestaurantSubscriptions, Long>, JpaSpecificationExecutor<RestaurantSubscriptions> {

    Optional<RestaurantSubscriptions> findByRestaurantIdAndIsTrialUsed(@Param("restaurantId") Long restaurantId, @Param("isTrialUsed") Boolean isTrialUsed);

    List<RestaurantSubscriptions> findByRestaurantIdAndStatusIn(@Param("restaurantId") Long restaurantId, @Param("statuses") List<String> statuses);

    @Query("SELECT rs FROM RestaurantSubscriptions rs WHERE rs.restaurant.id = :restaurantId AND rs.status IN ('trial', 'active')")
    List<RestaurantSubscriptions> findActiveSubscriptionsByRestaurantId(@Param("restaurantId") Long restaurantId);

    long countByStatus(String status);

    @Query("SELECT SUM(rs.finalAmount) FROM RestaurantSubscriptions rs WHERE rs.startDate >= :start AND rs.startDate < :end")
    BigDecimal sumFinalAmountByStartDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(rs.finalAmount) FROM RestaurantSubscriptions rs")
    BigDecimal sumFinalAmount();

    @Query("SELECT MONTH(rs.startDate) as month, SUM(rs.finalAmount) as amount FROM RestaurantSubscriptions rs GROUP BY MONTH(rs.startDate)")
    List<Object[]> findMonthlyRevenue();

    @Query("SELECT rs.plan.id as planId, COUNT(rs) as count FROM RestaurantSubscriptions rs GROUP BY rs.plan.id")
    List<Object[]> countByPlanId();

    @Query("SELECT COUNT(rs) FROM RestaurantSubscriptions rs WHERE rs.endDate IS NOT NULL AND rs.endDate > :thirtyDaysAgo AND (rs.status = 'cancelled' OR rs.status = 'EXPIRED')")
    long countChurnedLast30Days(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);
}
