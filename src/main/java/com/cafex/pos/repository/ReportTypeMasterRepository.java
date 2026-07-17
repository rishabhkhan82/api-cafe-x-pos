package com.cafex.pos.repository;

import com.cafex.pos.entity.ReportTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReportTypeMasterRepository extends JpaRepository<ReportTypeMaster, Long>, JpaSpecificationExecutor<ReportTypeMaster> {
    boolean existsByKey(String key);
    boolean existsByName(String name);
}
