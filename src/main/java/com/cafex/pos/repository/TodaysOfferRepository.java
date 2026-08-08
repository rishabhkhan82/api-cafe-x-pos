package com.cafex.pos.repository;

import com.cafex.pos.entity.TodaysOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TodaysOfferRepository extends JpaRepository<TodaysOffer, Long>, JpaSpecificationExecutor<TodaysOffer> {
}
