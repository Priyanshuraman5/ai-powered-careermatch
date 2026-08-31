package com.careermatch.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserProfileDto {
    private Long id;
    private String userId;
    private String fullName;
    private String headline;
    private String bio;
    private List<String> skills; 
    private String experienceLevel;
    private String location;
    private String phone;
    private String about;
    private String profileImage;
    private String resumeName;
    private String resumeUrl;
    private List<CertificateFile> certificateFiles;

    @Data
    public static class CertificateFile {
        private String name;
        private String url;
    }

    // ADD THESE TO PREVENT REACT STATE WIPEOUTS:
    private Object education;
    private Object certifications;
    private Object experience;
    private Object projects;
    private Object achievements;
    private Object codingProfiles;
}