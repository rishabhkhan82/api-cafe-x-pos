package com.cafex.pos.repository;

import com.cafex.pos.entity.EmailServiceConfigurations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailServiceConfigurationsRepository extends JpaRepository<EmailServiceConfigurations, Long> {
}
