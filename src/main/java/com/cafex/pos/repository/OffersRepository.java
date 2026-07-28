package com.cafex.pos.repository;

import com.cafex.pos.entity.Offers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OffersRepository extends JpaRepository<Offers, Long>, JpaSpecificationExecutor<Offers> {
    boolean existsByOfferId(String offerId);
    boolean existsByOfferIdAndIdNot(String offerId, Long id);

    @Query("SELECT o.offerId FROM Offers o WHERE o.offerId LIKE CONCAT(:prefix, '%')")
    List<String> findOfferIdsStartingWith(@Param("prefix") String prefix);
}