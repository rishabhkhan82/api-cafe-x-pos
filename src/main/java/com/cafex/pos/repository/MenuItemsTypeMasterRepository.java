package com.cafex.pos.repository;

import com.cafex.pos.entity.MenuItemsTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MenuItemsTypeMasterRepository extends JpaRepository<MenuItemsTypeMaster, Long>, JpaSpecificationExecutor<MenuItemsTypeMaster> {
    boolean existsByKey(String key);
    boolean existsByName(String name);
}
