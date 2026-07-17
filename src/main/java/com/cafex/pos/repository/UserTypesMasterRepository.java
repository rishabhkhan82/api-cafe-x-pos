package com.cafex.pos.repository;

import com.cafex.pos.entity.UserTypesMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserTypesMasterRepository extends JpaRepository<UserTypesMaster, Long>, JpaSpecificationExecutor<UserTypesMaster> {
    boolean existsByKey(String key);
    boolean existsByName(String name);
}
