package com.cafex.pos.repository;

import com.cafex.pos.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long>, JpaSpecificationExecutor<InventoryItem> {
    boolean existsByItemId(String itemId);
    boolean existsByItemIdAndIdNot(String itemId, Long id);

    List<InventoryItem> findByRestaurantId(Long restaurantId);

    @Query("SELECT i FROM InventoryItem i WHERE i.restaurant.id = :restaurantId AND i.currentStock <= i.minimumStock AND i.currentStock > 0")
    List<InventoryItem> findLowStockByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("SELECT i FROM InventoryItem i WHERE i.restaurant.id = :restaurantId AND i.currentStock <= 0")
    List<InventoryItem> findOutOfStockByRestaurantId(@Param("restaurantId") Long restaurantId);
}
