package com.cafex.pos.repository;

import com.cafex.pos.entity.NavigationMenuTypesMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NavigationMenuTypesMasterRepository extends JpaRepository<NavigationMenuTypesMaster, Long>, JpaSpecificationExecutor<NavigationMenuTypesMaster> {
    boolean existsByKey(String key);
    boolean existsByName(String name);
}
