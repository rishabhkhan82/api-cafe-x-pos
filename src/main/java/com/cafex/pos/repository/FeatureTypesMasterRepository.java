package com.cafex.pos.repository;

import com.cafex.pos.entity.FeatureTypesMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FeatureTypesMasterRepository extends JpaRepository<FeatureTypesMaster, Long>, JpaSpecificationExecutor<FeatureTypesMaster> {
    boolean existsByKey(String key);
    boolean existsByName(String name);
}
