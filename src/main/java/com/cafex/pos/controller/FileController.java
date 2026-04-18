package com.cafex.pos.controller;

import com.cafex.pos.dto.OperationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class FileController {

    @PostMapping("/upload")
    public ResponseEntity<OperationResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("category") String category,
            @RequestParam(value = "entityId", required = false) String entityId) {

        log.info("File upload request received - category: {}, entityId: {}, filename: {}", category, entityId, file.getOriginalFilename());

        try {
            // Validate file
            if (file.isEmpty()) {
                OperationResponse operationResponse = new OperationResponse("failure", "FILE_EMPTY", null, null);
                return ResponseEntity.badRequest().body(operationResponse);
            }

            // Determine upload directory based on category
            String uploadDir = getUploadDirectory(category);
            if (uploadDir == null) {
                OperationResponse operationResponse = new OperationResponse("failure", "INVALID_CATEGORY", null, null);
                return ResponseEntity.badRequest().body(operationResponse);
            }

            // Create directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID().toString() + (fileExtension != null ? "." + fileExtension : "");
            Path filePath = uploadPath.resolve(uniqueFilename);

            // Save file
            Files.copy(file.getInputStream(), filePath);

            // Generate URL
            String fileUrl = "/" + uploadDir.replace("\\", "/") + "/" + uniqueFilename;

            log.info("File uploaded successfully: {}", fileUrl);

            // Create response data
            FileUploadResponse responseData = new FileUploadResponse(
                uniqueFilename,
                originalFilename,
                file.getSize(),
                file.getContentType(),
                fileUrl,
                category,
                entityId,
                LocalDateTime.now()
            );

            OperationResponse operationResponse = new OperationResponse("success", "FILE_UPLOADED", null, responseData);
            return ResponseEntity.ok(operationResponse);

        } catch (IOException e) {
            log.error("Failed to upload file: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "FILE_UPLOAD_FAILED", null, null);
            return ResponseEntity.badRequest().body(operationResponse);
        } catch (Exception e) {
            log.error("Unexpected error during file upload: {}", e.getMessage());
            OperationResponse operationResponse = new OperationResponse("failure", "INTERNAL_ERROR", null, null);
            return ResponseEntity.internalServerError().body(operationResponse);
        }
    }

    private String getUploadDirectory(String category) {
        switch (category) {
            case "menu_image":
                return "uploads/images/menu";
            case "profile":
                return "uploads/images/profile";
            case "receipt":
                return "uploads/documents/receipts";
            case "document":
                return "uploads/documents";
            case "report":
                return "uploads/reports";
            default:
                return null; // Invalid category
        }
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        }
        return null;
    }

    // Inner class for response data
    public static class FileUploadResponse {
        private String id;
        private String name;
        private long size;
        private String mimeType;
        private String url;
        private String category;
        private String entityId;
        private LocalDateTime uploadedAt;

        public FileUploadResponse(String id, String name, long size, String mimeType, String url, String category, String entityId, LocalDateTime uploadedAt) {
            this.id = id;
            this.name = name;
            this.size = size;
            this.mimeType = mimeType;
            this.url = url;
            this.category = category;
            this.entityId = entityId;
            this.uploadedAt = uploadedAt;
        }

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public long getSize() { return size; }
        public String getMimeType() { return mimeType; }
        public String getUrl() { return url; }
        public String getCategory() { return category; }
        public String getEntityId() { return entityId; }
        public LocalDateTime getUploadedAt() { return uploadedAt; }
    }
}