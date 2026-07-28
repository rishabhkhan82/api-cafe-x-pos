package com.cafex.pos.repository;

import com.cafex.pos.entity.WasteManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WasteManagementRepository extends JpaRepository<WasteManagement, Long>, JpaSpecificationExecutor<WasteManagement> {
}
