package com.cafex.pos.repository;

import com.cafex.pos.entity.OrderItemAddon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderItemAddonRepository extends JpaRepository<OrderItemAddon, Long> {
    List<OrderItemAddon> findByOrderItemId(Long orderItemId);
    void deleteByOrderItemId(Long orderItemId);
}
