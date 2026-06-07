package com.cafex.pos.repository;

import com.cafex.pos.entity.OfferRedemptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRedemptionsRepository extends JpaRepository<OfferRedemptions, Long>, JpaSpecificationExecutor<OfferRedemptions> {
    Optional<OfferRedemptions> findByRedemptionId(String redemptionId);
    boolean existsByRedemptionId(String redemptionId);
    List<OfferRedemptions> findByCustomerId(Long customerId);
    List<OfferRedemptions> findByOfferId(Long offerId);
    Page<OfferRedemptions> findByCustomerId(Long customerId, Pageable pageable);
    Page<OfferRedemptions> findByRestaurantId(Long restaurantId, Pageable pageable);
}
