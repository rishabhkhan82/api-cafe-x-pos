package com.cafex.pos.scheduler;

import com.cafex.pos.entity.SystemSetting;
import com.cafex.pos.service.EmailService;
import com.cafex.pos.service.SystemSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DbBackupScheduler {

    private final SystemSettingsService systemSettingsService;
    private final EmailService emailService;
    private final Environment environment;

    @Value("${backup.upload-dir:uploads/backup}")
    private String uploadDir;

    @Value("${backup.mysqldump-path:mysqldump}")
    private String mysqldumpPath;

    @Value("${backup.retention-days:30}")
    private int retentionDays;

    @Value("${backup.download-base-url:http://localhost:8080/api}")
    private String downloadBaseUrl;

    @Scheduled(cron = "0 0 2 * * ?")
    public void performBackup() {
        try {
            SystemSetting settings = systemSettingsService.getSystemSettings();
            if (settings == null || !Boolean.TRUE.equals(settings.getBackupEnabled())) {
                log.info("Database backup is disabled. Skipping.");
                return;
            }

            String frequency = settings.getBackupFrequency();
            LocalDateTime now = LocalDateTime.now();
            boolean shouldRun = false;

            if (frequency == null) {
                log.warn("Backup frequency is not set. Skipping.");
                return;
            }

            switch (frequency.toUpperCase()) {
                case "DAILY" -> shouldRun = true;
                case "WEEKLY" -> shouldRun = now.getDayOfWeek() == DayOfWeek.SUNDAY;
                case "MONTHLY" -> shouldRun = now.getDayOfMonth() == 1;
                default -> {
                    log.warn("Unknown backup frequency: {}. Skipping.", frequency);
                    return;
                }
            }

            if (!shouldRun) {
                log.info("Skipping backup based on frequency: {} and date: {}", frequency, now);
                return;
            }

            log.info("Starting database backup... Frequency: {}, Time: {}", frequency, now);

            String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "cafe_x_pos_backup_" + timestamp + ".sql";
            Path backupPath = Paths.get(uploadDir, filename);
            Files.createDirectories(backupPath.getParent());

            String url = environment.getProperty("spring.datasource.url");
            String username = environment.getProperty("spring.datasource.username");
            String password = environment.getProperty("spring.datasource.password");

            if (url == null || username == null || password == null) {
                log.error("Database configuration is missing. Cannot perform backup.");
                return;
            }

            String dbUrl = url.replace("jdbc:mysql://", "").split("\\?")[0];
            String[] urlParts = dbUrl.split("/");
            String hostPort = urlParts.length > 0 ? urlParts[0] : "localhost:3306";
            String dbName = urlParts.length > 1 ? urlParts[1] : "cafe_x_pos";

            String host = hostPort;
            String port = "3306";
            if (hostPort.contains(":")) {
                String[] hp = hostPort.split(":");
                host = hp[0];
                port = hp.length > 1 ? hp[1] : "3306";
            }

            ProcessBuilder pb = new ProcessBuilder();
            pb.command(mysqldumpPath, "-h", host, "-P", port, "-u", username, dbName);
            Map<String, String> env = pb.environment();
            env.put("MYSQL_PWD", password);

            Process process = pb.start();

            StringBuilder errorOutput = new StringBuilder();
            Thread errorThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorOutput.append(line).append("\n");
                    }
                } catch (IOException e) {
                    log.error("Error reading mysqldump stderr", e);
                }
            });
            errorThread.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedWriter writer = Files.newBufferedWriter(backupPath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            int exitCode = process.waitFor();
            errorThread.join();

            if (exitCode == 0 && Files.exists(backupPath) && Files.size(backupPath) > 0) {
                log.info("Database backup completed successfully: {}", backupPath);

                String downloadUrl = downloadBaseUrl + "/uploads/backup/" + filename;
                sendBackupNotification(settings, filename, backupPath.toString(), downloadUrl, true, null);
                cleanupOldBackups();
            } else {
                String errorMsg = errorOutput.toString().trim();
                if (errorMsg.isEmpty()) {
                    errorMsg = "mysqldump exited with code " + exitCode;
                }
                log.error("Database backup failed with exit code: {}. Error: {}", exitCode, errorMsg);
                sendBackupNotification(settings, filename, backupPath.toString(), null, false, errorMsg);
            }

        } catch (Exception e) {
            log.error("Unexpected error during database backup", e);
            try {
                SystemSetting settings = systemSettingsService.getSystemSettings();
                if (settings != null && settings.getSupportEmail() != null && !settings.getSupportEmail().isEmpty()) {
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                    String filename = "cafe_x_pos_backup_" + timestamp + ".sql";
                    sendBackupNotification(settings, filename, null, null, false, e.getMessage());
                }
            } catch (Exception ex) {
                log.error("Failed to send backup failure notification", ex);
            }
        }
    }

    private void sendBackupNotification(SystemSetting settings, String filename, String filePath, String downloadUrl, boolean success, String errorMessage) {
        if (settings.getSupportEmail() == null || settings.getSupportEmail().isEmpty()) {
            log.warn("Support email is not configured. Skipping notification.");
            return;
        }

        String subject = success ? "Database Backup Successful" : "Database Backup Failed";
        Map<String, Object> variables = new HashMap<>();
        variables.put("platformName", settings.getPlatformName() != null ? settings.getPlatformName() : "CafeX POS");
        variables.put("filename", filename);
        variables.put("backupDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
        variables.put("success", success);
        variables.put("downloadUrl", downloadUrl);
        variables.put("errorMessage", errorMessage != null ? errorMessage : "N/A");
        variables.put("retentionDays", retentionDays);

        emailService.sendHtmlEmail(settings.getSupportEmail(), subject, "backup-notification", variables);
    }

    private void cleanupOldBackups() {
        try {
            Path backupDir = Paths.get(uploadDir);
            if (!Files.exists(backupDir)) {
                return;
            }

            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

            Files.list(backupDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".sql"))
                    .forEach(path -> {
                        try {
                            String filename = path.getFileName().toString();
                            String timestampStr = filename.replace("cafe_x_pos_backup_", "").replace(".sql", "");
                            LocalDateTime fileDate = LocalDateTime.parse(timestampStr, formatter);

                            if (fileDate.isBefore(cutoffDate)) {
                                Files.delete(path);
                                log.info("Deleted old backup: {}", filename);
                            }
                        } catch (Exception e) {
                            log.error("Failed to delete old backup: {}", path, e);
                        }
                    });
        } catch (Exception e) {
            log.error("Error during backup cleanup", e);
        }
    }
}
