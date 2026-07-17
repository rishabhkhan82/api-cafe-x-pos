package com.cafex.pos.repository;

import com.cafex.pos.entity.OrderTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderTypeMasterRepository extends JpaRepository<OrderTypeMaster, Long>, JpaSpecificationExecutor<OrderTypeMaster> {
    boolean existsByKey(String key);
    boolean existsByName(String name);
}
