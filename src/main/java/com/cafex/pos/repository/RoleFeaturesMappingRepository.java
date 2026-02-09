package com.cafex.pos.repository;

import com.cafex.pos.entity.RoleFeaturesMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleFeaturesMappingRepository extends JpaRepository<RoleFeaturesMapping, Long>, JpaSpecificationExecutor<RoleFeaturesMapping> {

    Optional<RoleFeaturesMapping> findById(Long id);

    boolean existsByPlanIdAndRoleIdAndFeatureId(Long planId, Long roleId, String featureId);
}
