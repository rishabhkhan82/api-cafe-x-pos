package com.cafex.pos.repository;

import com.cafex.pos.entity.MenuCategoriesMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MenuCategoriesMasterRepository extends JpaRepository<MenuCategoriesMaster, Long>, JpaSpecificationExecutor<MenuCategoriesMaster> {
    boolean existsByKey(String key);
    boolean existsByName(String name);
}
