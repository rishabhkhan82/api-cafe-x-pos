package com.cafex.pos.repository;

import com.cafex.pos.entity.WasteReasonTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WasteReasonTypeMasterRepository extends JpaRepository<WasteReasonTypeMaster, Long>, JpaSpecificationExecutor<WasteReasonTypeMaster> {
    boolean existsByKey(String key);
    boolean existsByName(String name);
}
