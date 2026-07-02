package com.cafex.pos.repository;

import com.cafex.pos.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerId(String customerId);
    Optional<Customer> findByEmail(String email);

    long count();

    long countByCreatedAtAfter(java.time.LocalDateTime createdAt);
}
