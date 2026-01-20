package com.cafex.pos.repository;

import com.cafex.pos.entity.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRolesRepository extends JpaRepository<UserRoles, Long>, JpaSpecificationExecutor<UserRoles> {

    Optional<UserRoles> findByRoleId(String roleId);

    boolean existsByRoleId(String roleId);

    @Query("SELECT ur FROM UserRoles ur")
    List<UserRoles> findAllWithoutPermissions();

    @Query("SELECT ur FROM UserRoles ur WHERE ur.id = :id")
    Optional<UserRoles> findByIdWithoutPermissions(Long id);

    @Query("SELECT ur FROM UserRoles ur WHERE ur.id = :id")
    Optional<UserRoles> findByIdWithPermissions(Long id);
}