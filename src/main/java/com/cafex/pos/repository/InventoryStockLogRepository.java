package com.cafex.pos.repository;

import com.cafex.pos.entity.InventoryStockLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InventoryStockLogRepository extends JpaRepository<InventoryStockLog, Long>, JpaSpecificationExecutor<InventoryStockLog> {
}
