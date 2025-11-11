package com.cafex.pos.repository;

import com.cafex.pos.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemSettingsRepository extends JpaRepository<SystemSetting, String> {

    /**
     * Find all public settings (accessible before login)
     */
    List<SystemSetting> findByIsPublicTrue();

    /**
     * Find settings by category
     */
    List<SystemSetting> findByCategory(String category);

    /**
     * Find public settings by category
     */
    List<SystemSetting> findByCategoryAndIsPublicTrue(String category);

    /**
     * Find settings that require restart
     */
    List<SystemSetting> findByRequiresRestartTrue();
}