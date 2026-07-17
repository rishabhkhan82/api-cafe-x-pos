package com.cafex.pos.repository;

import com.cafex.pos.entity.WasteTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WasteTypeMasterRepository extends JpaRepository<WasteTypeMaster, Long>, JpaSpecificationExecutor<WasteTypeMaster> {
    boolean existsByKey(String key);
    boolean existsByName(String name);
}
