package com.cafex.pos.repository;

import com.cafex.pos.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long>, JpaSpecificationExecutor<MenuItem> {
    boolean existsByItemId(String itemId);
    boolean existsByItemIdAndIdNot(String itemId, Long id);

    @Query("SELECT COUNT(mi) FROM MenuItem mi WHERE mi.restaurantId = :restaurantId AND mi.category = :categoryKey")
    long countByRestaurantIdAndCategory(@Param("restaurantId") Long restaurantId, @Param("categoryKey") String categoryKey);
}
