package com.cafex.pos.repository;

import com.cafex.pos.entity.RestaurantSubscriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantSubscriptionRepository extends JpaRepository<RestaurantSubscriptions, Long>, JpaSpecificationExecutor<RestaurantSubscriptions> {

    // Trial-related queries
    Optional<RestaurantSubscriptions> findByRestaurantIdAndIsTrialUsed(@Param("restaurantId") Long restaurantId, @Param("isTrialUsed") Boolean isTrialUsed);

    List<RestaurantSubscriptions> findByRestaurantIdAndStatusIn(@Param("restaurantId") Long restaurantId, @Param("statuses") List<String> statuses);

    // Find active subscriptions (trial or active status)
    @Query("SELECT rs FROM RestaurantSubscriptions rs WHERE rs.restaurant.id = :restaurantId AND rs.status IN ('trial', 'active')")
    List<RestaurantSubscriptions> findActiveSubscriptionsByRestaurantId(@Param("restaurantId") Long restaurantId);
}