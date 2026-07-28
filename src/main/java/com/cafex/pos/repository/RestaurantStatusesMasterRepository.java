package com.cafex.pos.repository;

import com.cafex.pos.entity.RestaurantStatusesMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RestaurantStatusesMasterRepository extends JpaRepository<RestaurantStatusesMaster, Long>, JpaSpecificationExecutor<RestaurantStatusesMaster> {
    boolean existsByKey(String key);
    boolean existsByName(String name);
}
