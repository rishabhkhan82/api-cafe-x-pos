package com.cafex.pos.repository;

import com.cafex.pos.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, JpaSpecificationExecutor<Restaurant> {

    Optional<Restaurant> findByEmail(String email);

    boolean existsByEmail(String email);

    long count();

    long countByCreatedAtAfter(java.time.LocalDateTime createdAt);

    long countByStatus(Restaurant.RestaurantStatus status);

    @Query("SELECT COUNT(r) FROM Restaurant r WHERE r.isActive = true OR r.status = 'ACTIVE'")
    long countActive();
}
