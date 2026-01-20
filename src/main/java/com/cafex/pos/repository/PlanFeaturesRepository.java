package com.cafex.pos.repository;

import com.cafex.pos.entity.PlanFeatures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PlanFeaturesRepository extends JpaRepository<PlanFeatures, Long>, JpaSpecificationExecutor<PlanFeatures> {

    Optional<PlanFeatures> findById(Long id);

    Optional<PlanFeatures> findByFeatureId(String featureId);

    boolean existsByFeatureId(String featureId);
}