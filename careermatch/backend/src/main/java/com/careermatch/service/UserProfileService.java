package com.careermatch.service;

import com.careermatch.model.dto.UserProfileDto;
import com.careermatch.model.entity.UserProfile;
import com.careermatch.repository.UserProfileRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository profileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public UserProfileDto getProfileByUserId(String userId) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElse(new UserProfile());
        return mapToDto(profile);
    }

    @Transactional
    public UserProfileDto saveOrUpdateProfile(String userId, UserProfileDto profileDto) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElse(new UserProfile());

        profile.setUserId(userId);

        // Standard Text Fields (Null-Safe)
        if (profileDto.getFullName() != null) profile.setFullName(profileDto.getFullName());
        if (profileDto.getHeadline() != null) profile.setHeadline(profileDto.getHeadline());
        if (profileDto.getBio() != null) profile.setBio(profileDto.getBio());
        if (profileDto.getExperienceLevel() != null) profile.setExperienceLevel(profileDto.getExperienceLevel());
        if (profileDto.getLocation() != null) profile.setLocation(profileDto.getLocation());
        if (profileDto.getPhone() != null) profile.setPhone(profileDto.getPhone());
        if (profileDto.getAbout() != null) profile.setAbout(profileDto.getAbout());
        if (profileDto.getProfileImage() != null) profile.setProfileImage(profileDto.getProfileImage());
        if (profileDto.getResumeName() != null) profile.setResumeName(profileDto.getResumeName());

        // Array & Object Serialization to JSON Strings
        if (profileDto.getSkills() != null) profile.setSkills(toJson(profileDto.getSkills()));
        if (profileDto.getEducation() != null) profile.setEducationJson(toJson(profileDto.getEducation()));
        if (profileDto.getCertifications() != null) profile.setCertificationsJson(toJson(profileDto.getCertifications()));
        if (profileDto.getExperience() != null) profile.setExperienceJson(toJson(profileDto.getExperience()));
        if (profileDto.getProjects() != null) profile.setProjectsJson(toJson(profileDto.getProjects()));
        if (profileDto.getAchievements() != null) profile.setAchievementsJson(toJson(profileDto.getAchievements()));
        if (profileDto.getCodingProfiles() != null) profile.setCodingProfilesJson(toJson(profileDto.getCodingProfiles()));

        UserProfile savedProfile = profileRepository.save(profile);
        return mapToDto(savedProfile);
    }

    /**
     * Saves an uploaded profile picture to disk and persists its URL.
     * Replaces the old approach of storing a giant Base64 string.
     */
    @Transactional
    public UserProfileDto saveProfilePicture(String userId, MultipartFile file) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserProfile p = new UserProfile();
                    p.setUserId(userId);
                    return p;
                });

        // best-effort cleanup of the old file so disk doesn't fill up
        fileStorageService.deleteByUrl(profile.getProfileImage());

        String url = fileStorageService.store(file, userId, "picture");
        profile.setProfileImage(url);

        return mapToDto(profileRepository.save(profile));
    }

    /**
     * Saves an uploaded resume file to disk and persists its name + URL.
     * Previously the frontend only sent the filename string - the actual
     * file bytes were never uploaded anywhere.
     */
    @Transactional
    public UserProfileDto saveResume(String userId, MultipartFile file) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserProfile p = new UserProfile();
                    p.setUserId(userId);
                    return p;
                });

        fileStorageService.deleteByUrl(profile.getResumeUrl());

        String url = fileStorageService.store(file, userId, "resume");
        profile.setResumeName(file.getOriginalFilename());
        profile.setResumeUrl(url);

        return mapToDto(profileRepository.save(profile));
    }

    /**
     * Uploads one or more certificate documents (PDF/image) and appends them
     * to the user's list of certificate files.
     */
    @Transactional
    public UserProfileDto addCertificateFiles(String userId, List<MultipartFile> files) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserProfile p = new UserProfile();
                    p.setUserId(userId);
                    return p;
                });

        List<UserProfileDto.CertificateFile> existing = fromJson(
                profile.getCertificateFilesJson(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, UserProfileDto.CertificateFile.class));
        if (existing == null) existing = new ArrayList<>();

        for (MultipartFile file : files) {
            String url = fileStorageService.store(file, userId, "certificates");
            UserProfileDto.CertificateFile cf = new UserProfileDto.CertificateFile();
            cf.setName(file.getOriginalFilename());
            cf.setUrl(url);
            existing.add(cf);
        }

        profile.setCertificateFilesJson(toJson(existing));

        return mapToDto(profileRepository.save(profile));
    }

    private UserProfileDto mapToDto(UserProfile profile) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUserId());
        dto.setFullName(profile.getFullName());
        dto.setHeadline(profile.getHeadline());
        dto.setBio(profile.getBio());
        dto.setExperienceLevel(profile.getExperienceLevel());
        dto.setLocation(profile.getLocation());
        dto.setPhone(profile.getPhone());
        dto.setAbout(profile.getAbout());
        dto.setProfileImage(profile.getProfileImage());
        dto.setResumeName(profile.getResumeName());
        dto.setResumeUrl(profile.getResumeUrl());
        dto.setCertificateFiles(fromJson(
                profile.getCertificateFilesJson(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, UserProfileDto.CertificateFile.class)));

        // Deserialization from JSON Strings back to JS Objects/Arrays
        dto.setSkills(fromJson(profile.getSkills(), List.class));
        dto.setEducation(fromJson(profile.getEducationJson(), Object.class));
        dto.setCertifications(fromJson(profile.getCertificationsJson(), Object.class));
        dto.setExperience(fromJson(profile.getExperienceJson(), Object.class));
        dto.setProjects(fromJson(profile.getProjectsJson(), Object.class));
        dto.setAchievements(fromJson(profile.getAchievementsJson(), Object.class));
        dto.setCodingProfiles(fromJson(profile.getCodingProfilesJson(), Object.class));

        return dto;
    }

    private String toJson(Object data) {
        try {
            return data != null ? objectMapper.writeValueAsString(data) : null;
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private <T> T fromJson(String json, Class<T> targetType) {
        try {
            return json != null ? objectMapper.readValue(json, targetType) : null;
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private <T> T fromJson(String json, com.fasterxml.jackson.databind.JavaType targetType) {
        try {
            return json != null ? objectMapper.readValue(json, targetType) : null;
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}