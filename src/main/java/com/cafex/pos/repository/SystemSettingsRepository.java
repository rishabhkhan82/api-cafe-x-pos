package com.cafex.pos.repository;

import com.cafex.pos.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemSettingsRepository extends JpaRepository<SystemSetting, Long> {
    // Repository for single SystemSetting entity
}