package com.cafex.pos.repository;

import com.cafex.pos.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long>, JpaSpecificationExecutor<MenuItem> {
    boolean existsByItemId(String itemId);
    boolean existsByItemIdAndIdNot(String itemId, Long id);
}