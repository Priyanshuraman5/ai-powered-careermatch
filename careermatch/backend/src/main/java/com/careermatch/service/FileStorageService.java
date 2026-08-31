package com.careermatch.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Persists uploaded files (profile pictures, resumes, certificates) to a real
 * directory on disk and hands back a public URL that can be stored in MySQL
 * and reused by the frontend after refresh / server restart.
 *
 * Root cause this fixes: the frontend was previously Base64-encoding files
 * (profile picture) or not uploading them at all (resume, certificates), so
 * nothing durable was ever written anywhere.
 */
@Service
public class FileStorageService {

    // Absolute or relative path on the SERVER filesystem where files are kept.
    // Configure in application.yml as app.upload.dir
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // Public URL prefix the frontend will use to fetch the file (served as a
    // static resource - see FileStorageConfig)
    @Value("${app.upload.base-url:/uploads}")
    private String baseUrl;

    private Path resolveRoot() {
        Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + path, e);
        }
        return path;
    }

    /**
     * Stores a file under {uploadDir}/{userId}/{subFolder}/{uuid}_{originalName}
     * and returns the public URL, e.g. /uploads/42/resume/9f1c_resume.pdf
     */
    public String store(MultipartFile file, String userId, String subFolder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store an empty file");
        }

        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        // strip anything that isn't a safe filename character to avoid path traversal
        String safeName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
        String storedName = UUID.randomUUID() + "_" + safeName;

        try {
            Path userDir = resolveRoot().resolve(userId).resolve(subFolder).normalize();
            Files.createDirectories(userDir);

            Path targetPath = userDir.resolve(storedName).normalize();
            if (!targetPath.startsWith(userDir)) {
                throw new SecurityException("Invalid file path detected");
            }

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return baseUrl + "/" + userId + "/" + subFolder + "/" + storedName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + originalName, e);
        }
    }

    /**
     * Deletes a previously stored file given its public URL. Safe to call with
     * null/blank - does nothing in that case. Never throws on missing files.
     */
    public void deleteByUrl(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank() || !publicUrl.startsWith(baseUrl)) {
            return;
        }
        try {
            String relative = publicUrl.substring(baseUrl.length()).replaceFirst("^/", "");
            Path target = resolveRoot().resolve(relative).normalize();
            if (target.startsWith(resolveRoot())) {
                Files.deleteIfExists(target);
            }
        } catch (IOException ignored) {
            // best-effort cleanup only
        }
    }
}
