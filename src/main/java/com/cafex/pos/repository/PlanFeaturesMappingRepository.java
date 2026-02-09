package com.cafex.pos.repository;

import com.cafex.pos.entity.PlanFeaturesMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PlanFeaturesMappingRepository extends JpaRepository<PlanFeaturesMapping, Long>, JpaSpecificationExecutor<PlanFeaturesMapping> {

    Optional<PlanFeaturesMapping> findById(Long id);

    boolean existsByPlanIdAndFeatureId(Long planId, String featureId);
}