package com.cafex.pos.repository;

import com.cafex.pos.entity.InventoryItemUnitsMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InventoryItemUnitsMasterRepository extends JpaRepository<InventoryItemUnitsMaster, Long>, JpaSpecificationExecutor<InventoryItemUnitsMaster> {
    boolean existsByKey(String key);
    boolean existsByName(String name);
}
