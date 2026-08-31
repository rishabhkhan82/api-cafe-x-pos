package com.cafex.pos.repository;

import com.cafex.pos.entity.MenuItemAddon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MenuItemAddonRepository extends JpaRepository<MenuItemAddon, Long>, JpaSpecificationExecutor<MenuItemAddon> {
    List<MenuItemAddon> findByMenuItemId(Long menuItemId);
    List<MenuItemAddon> findByMenuItemIdAndAddonId(Long menuItemId, Long addonId);
    Optional<MenuItemAddon> findByMenuItemIdAndAddonIdAndIdNot(Long menuItemId, Long addonId, Long id);
    boolean existsByMenuItemIdAndAddonId(Long menuItemId, Long addonId);
    @Modifying
    @Transactional
    void deleteByMenuItemId(Long menuItemId);
    @Modifying
    @Transactional
    void deleteByMenuItemIdAndAddonId(Long menuItemId, Long addonId);
}
