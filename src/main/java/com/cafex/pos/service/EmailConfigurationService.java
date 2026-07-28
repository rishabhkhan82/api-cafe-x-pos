package com.cafex.pos.service;

import com.cafex.pos.entity.EmailServiceConfigurations;
import com.cafex.pos.exception.ResourceNotFoundException;
import com.cafex.pos.repository.EmailServiceConfigurationsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmailConfigurationService {

    private final EmailServiceConfigurationsRepository configurationRepository;

    public List<EmailServiceConfigurations> getAllConfigurations() {
        return configurationRepository.findAll();
    }

    public Optional<EmailServiceConfigurations> getConfigurationById(Long id) {
        return configurationRepository.findById(id);
    }

    public EmailServiceConfigurations createConfiguration(EmailServiceConfigurations configuration) {
        configuration.setCreatedAt(java.time.LocalDateTime.now());
        configuration.setUpdatedAt(java.time.LocalDateTime.now());
        return configurationRepository.save(configuration);
    }

    public EmailServiceConfigurations updateConfiguration(Long id, EmailServiceConfigurations updatedConfig) {
        return configurationRepository.findById(id)
                .map(existing -> {
                    existing.setProvider(updatedConfig.getProvider());
                    existing.setApiKey(updatedConfig.getApiKey());
                    existing.setFromEmail(updatedConfig.getFromEmail());
                    existing.setFromName(updatedConfig.getFromName());
                    existing.setSmtpHost(updatedConfig.getSmtpHost());
                    existing.setSmtpPort(updatedConfig.getSmtpPort());
                    existing.setSmtpUsername(updatedConfig.getSmtpUsername());
                    existing.setSmtpPassword(updatedConfig.getSmtpPassword());
                    existing.setTemplates(updatedConfig.getTemplates());
                    existing.setUpdatedBy(updatedConfig.getUpdatedBy());
                    existing.setUpdatedAt(java.time.LocalDateTime.now());
                    return configurationRepository.save(existing);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Email configuration not found with ID: " + id));
    }

    public void deleteConfiguration(Long id) {
        configurationRepository.deleteById(id);
    }
}
