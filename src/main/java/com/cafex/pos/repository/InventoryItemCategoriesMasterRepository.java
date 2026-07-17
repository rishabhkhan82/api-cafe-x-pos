package com.cafex.pos.repository;

import com.cafex.pos.entity.InventoryItemCategoriesMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InventoryItemCategoriesMasterRepository extends JpaRepository<InventoryItemCategoriesMaster, Long>, JpaSpecificationExecutor<InventoryItemCategoriesMaster> {
    boolean existsByKey(String key);
    boolean existsByName(String name);
}
