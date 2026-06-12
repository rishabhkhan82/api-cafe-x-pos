package com.cafex.pos.repository;

import com.cafex.pos.entity.Order;
import com.cafex.pos.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByOrderId(String orderId);
    boolean existsByOrderId(String orderId);
    List<Order> findByCustomerId(Long customerId);
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);
}