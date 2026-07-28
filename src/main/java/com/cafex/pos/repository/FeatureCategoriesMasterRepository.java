package com.cafex.pos.repository;

import com.cafex.pos.entity.FeatureCategoriesMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FeatureCategoriesMasterRepository extends JpaRepository<FeatureCategoriesMaster, Long>, JpaSpecificationExecutor<FeatureCategoriesMaster> {
    boolean existsByKey(String key);
    boolean existsByName(String name);
}
