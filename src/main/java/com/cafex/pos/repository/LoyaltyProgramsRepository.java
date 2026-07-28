package com.cafex.pos.repository;

import com.cafex.pos.entity.LoyaltyPrograms;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoyaltyProgramsRepository extends JpaRepository<LoyaltyPrograms, Long>, JpaSpecificationExecutor<LoyaltyPrograms> {
    Optional<LoyaltyPrograms> findByProgramId(String programId);
    boolean existsByProgramId(String programId);
    Optional<LoyaltyPrograms> findByCustomerId(Long customerId);
    List<LoyaltyPrograms> findByIsActive(Boolean isActive);
}
