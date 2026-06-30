package com.cafex.pos.repository;

import com.cafex.pos.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long>, JpaSpecificationExecutor<InventoryItem> {
    boolean existsByItemId(String itemId);
    boolean existsByItemIdAndIdNot(String itemId, Long id);
}
