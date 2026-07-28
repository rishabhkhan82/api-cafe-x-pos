package com.cafex.pos.repository;

import com.cafex.pos.entity.InventoryItemTypesMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InventoryItemTypesMasterRepository extends JpaRepository<InventoryItemTypesMaster, Long>, JpaSpecificationExecutor<InventoryItemTypesMaster> {
    boolean existsByKey(String key);
    boolean existsByName(String name);
}
