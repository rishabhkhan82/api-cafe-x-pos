package com.cafex.pos.repository;

import com.cafex.pos.entity.MenuAccessPermissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuAccessPermissionsRepository extends JpaRepository<MenuAccessPermissions, Long>, JpaSpecificationExecutor<MenuAccessPermissions> {

    Optional<MenuAccessPermissions> findByPermissionId(String permissionId);

    List<MenuAccessPermissions> findByRoleId(Long roleId);

    boolean existsByPermissionId(String permissionId);
}