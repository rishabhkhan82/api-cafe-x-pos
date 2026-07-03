package com.cafex.pos.repository;

import com.cafex.pos.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
    void deleteByOrderId(Long orderId);

    @Query(value = "SELECT oi.menu_item_name AS name, SUM(oi.quantity) AS orders, SUM(oi.total_price) AS revenue FROM order_items oi JOIN orders o ON oi.order_id = o.id WHERE o.restaurant_id = :restaurantId GROUP BY oi.menu_item_name ORDER BY orders DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findPopularItemsByRestaurantId(@Param("restaurantId") Long restaurantId);
}