package com.cafex.pos.repository;

import com.cafex.pos.entity.Order;
import com.cafex.pos.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByOrderId(String orderId);
    boolean existsByOrderId(String orderId);
    List<Order> findByCustomerId(Long customerId);
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);

    long countByStatus(Order.OrderStatus status);

    long countByStatusAndCreatedAtAfter(Order.OrderStatus status, LocalDateTime createdAt);

    long countByCreatedAtAfter(LocalDateTime createdAt);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status AND o.createdAt > :createdAt")
    BigDecimal sumTotalAmountByStatusAndCreatedAtAfter(@Param("status") Order.OrderStatus status, @Param("createdAt") LocalDateTime createdAt);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") Order.OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.restaurant.id = :restaurantId")
    long countByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.status = :status")
    long countByRestaurantIdAndStatus(@Param("restaurantId") Long restaurantId, @Param("status") Order.OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.createdAt > :createdAt")
    long countByRestaurantIdAndCreatedAtAfter(@Param("restaurantId") Long restaurantId, @Param("createdAt") java.time.LocalDateTime createdAt);

    @Query("SELECT COUNT(DISTINCT o.customerName) FROM Order o WHERE o.restaurant.id = :restaurantId")
    long countDistinctCustomersByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("SELECT COUNT(DISTINCT o.customerName) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.createdAt > :createdAt")
    long countDistinctCustomersByRestaurantIdAndCreatedAtAfter(@Param("restaurantId") Long restaurantId, @Param("createdAt") java.time.LocalDateTime createdAt);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.status = :status")
    BigDecimal sumTotalAmountByRestaurantIdAndStatus(@Param("restaurantId") Long restaurantId, @Param("status") Order.OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.status = :status AND o.createdAt > :createdAt")
    BigDecimal sumTotalAmountByRestaurantIdAndStatusAndCreatedAtAfter(@Param("restaurantId") Long restaurantId, @Param("status") Order.OrderStatus status, @Param("createdAt") java.time.LocalDateTime createdAt);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.status = :status AND o.createdAt > :createdAt")
    long countByRestaurantIdAndStatusAndCreatedAtAfter(@Param("restaurantId") Long restaurantId, @Param("status") Order.OrderStatus status, @Param("createdAt") java.time.LocalDateTime createdAt);

    @Query(value = "SELECT o.* FROM orders o WHERE o.restaurant_id = ?1 ORDER BY o.created_at DESC LIMIT 5", nativeQuery = true)
    List<com.cafex.pos.entity.Order> findTop5ByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);
}
