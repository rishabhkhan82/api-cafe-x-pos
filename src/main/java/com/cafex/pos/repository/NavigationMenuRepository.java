package com.cafex.pos.repository;

import com.cafex.pos.entity.NavigationMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NavigationMenuRepository extends JpaRepository<NavigationMenu, Long>, JpaSpecificationExecutor<NavigationMenu> {

    Optional<NavigationMenu> findById(Long id);

    Optional<NavigationMenu> findByMenuId(String menuId);

    boolean existsByMenuId(String menuId);
}