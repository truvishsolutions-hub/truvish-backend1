package com.truvish.truvishbackend.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDir;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public FileStorageService(
            @Value("${app.upload.dir:uploads}") String dir
    ) {

        this.uploadDir = Paths
                .get(dir)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not create upload directory: " + this.uploadDir,
                    e
            );
        }
    }


    // =========================================================
    // STORE FILE
    // =========================================================
    //
    // Saves the physical file inside:
    //
    // uploads/
    //
    // Returns ONLY the generated filename.
    //
    // Example:
    //
    // 8f7c2a1e-....png
    //
    // This filename can safely be stored in PostgreSQL.
    // =========================================================

    public String storeFile(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            originalFilename = "file";
        }

        // -----------------------------------------------------
        // Extract extension
        // -----------------------------------------------------

        String extension = "";

        int dotIndex = originalFilename.lastIndexOf('.');

        if (dotIndex >= 0 && dotIndex < originalFilename.length() - 1) {
            extension = originalFilename.substring(dotIndex);
        }

        // -----------------------------------------------------
        // Generate unique filename
        // -----------------------------------------------------

        String filename = UUID.randomUUID() + extension;

        // -----------------------------------------------------
        // Resolve target safely
        // -----------------------------------------------------

        Path target = uploadDir
                .resolve(filename)
                .normalize();

        // Extra protection: target must remain inside uploadDir
        if (!target.startsWith(uploadDir)) {
            throw new IOException("Invalid file path.");
        }

        // -----------------------------------------------------
        // Save file
        // -----------------------------------------------------

        try (var inputStream = file.getInputStream()) {

            Files.copy(
                    inputStream,
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        // -----------------------------------------------------
        // IMPORTANT:
        // Save only filename in DB.
        // -----------------------------------------------------

        return filename;
    }


    // =========================================================
    // LOAD FILE
    // =========================================================

    public Resource load(String filename) {

        try {

            if (filename == null || filename.isBlank()) {
                return null;
            }

            Path path = uploadDir
                    .resolve(filename)
                    .normalize();

            // Prevent path traversal
            if (!path.startsWith(uploadDir)) {
                return null;
            }

            Resource resource = new UrlResource(
                    path.toUri()
            );

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }

            return null;

        } catch (Exception e) {

            return null;
        }
    }


    // =========================================================
    // DELETE FILE
    // =========================================================

    public void delete(String filename) {

        try {

            if (filename == null || filename.isBlank()) {
                return;
            }

            Path path = uploadDir
                    .resolve(filename)
                    .normalize();

            // Prevent path traversal
            if (!path.startsWith(uploadDir)) {
                return;
            }

            Files.deleteIfExists(path);

        } catch (Exception ignored) {
            // Intentionally ignored.
            // File deletion failure should not break
            // the main database operation.
        }
    }
}