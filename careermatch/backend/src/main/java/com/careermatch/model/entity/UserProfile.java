package com.careermatch.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;
    
    private String fullName;
    private String headline;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(columnDefinition = "TEXT")
    private String skills; 
    
    private String experienceLevel;
    private String location;
    private String phone;
    
    @Column(columnDefinition = "TEXT")
    private String about;
    
    // Stores the PUBLIC URL of the uploaded picture (e.g. /uploads/12/picture/xyz.jpg)
    // NOTE: no longer stores base64 - the actual file lives on disk (see FileStorageService)
    @Column(length = 500)
    private String profileImage;
    
    private String resumeName;

    // Public URL of the uploaded resume file on disk
    @Column(length = 500)
    private String resumeUrl;

    // JSON array of {"name":..,"url":..} for uploaded certificate documents (files, not just metadata)
    @Column(columnDefinition = "LONGTEXT")
    private String certificateFilesJson;

    // Store array structures directly as JSON strings in SQL
    @Column(columnDefinition = "LONGTEXT")
    private String educationJson;

    @Column(columnDefinition = "LONGTEXT")
    private String certificationsJson;

    @Column(columnDefinition = "LONGTEXT")
    private String experienceJson;

    @Column(columnDefinition = "LONGTEXT")
    private String projectsJson;

    @Column(columnDefinition = "LONGTEXT")
    private String achievementsJson;

    @Column(columnDefinition = "LONGTEXT")
    private String codingProfilesJson;
}