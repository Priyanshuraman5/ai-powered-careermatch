package com.careermatch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Serves everything under app.upload.dir at the app.upload.base-url path
 * (default /uploads/**) as static files, so the profile picture, resume and
 * certificate URLs stored in MySQL are actually reachable by the browser.
 *
 * IMPORTANT: your SecurityConfig must permitAll() on this path (GETs only)
 * since <img src> / file download requests will NOT carry the JWT header.
 * e.g.  .requestMatchers("/uploads/**").permitAll()
 */
@Configuration
public class FileStorageConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.base-url:/uploads}")
    private String baseUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Paths.get(uploadDir).toAbsolutePath().normalize().toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }
        String pattern = baseUrl.endsWith("/") ? baseUrl + "**" : baseUrl + "/**";

        registry.addResourceHandler(pattern)
                .addResourceLocations(location);
    }
}
