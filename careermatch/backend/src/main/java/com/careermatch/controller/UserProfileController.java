package com.careermatch.controller;

import com.careermatch.model.dto.UserProfileDto;
import com.careermatch.model.entity.User;
import com.careermatch.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    /**
     * ROOT CAUSE FIX: JwtAuthFilter sets the Authentication's principal to
     * the User entity itself (not a String), so Principal.getName() was
     * calling User.toString() under the hood - an unstable value that
     * doesn't reliably match user_profiles.user_id between requests. That's
     * why saves looked successful but the data couldn't be found again on
     * refresh. @AuthenticationPrincipal injects that same User object
     * directly, so we use its email (the same key JwtAuthFilter looks users
     * up by) as the stable, consistent profile key.
     */
    private String resolveUserId(User currentUser) {
        return currentUser.getEmail();
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentProfile(@AuthenticationPrincipal User currentUser) {
        String userId = resolveUserId(currentUser);
        return ResponseEntity.ok(userProfileService.getProfileByUserId(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileDto> updateProfile(
            @AuthenticationPrincipal User currentUser,
            @RequestBody UserProfileDto profileDto) {
        String userId = resolveUserId(currentUser);
        return ResponseEntity.ok(userProfileService.saveOrUpdateProfile(userId, profileDto));
    }

    /**
     * Real file upload for the profile picture. Replaces the old flow where
     * the browser Base64-encoded the image and stuffed it into the JSON body.
     */
    @PostMapping(value = "/me/picture", consumes = "multipart/form-data")
    public ResponseEntity<UserProfileDto> uploadProfilePicture(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("file") MultipartFile file) {
        String userId = resolveUserId(currentUser);
        return ResponseEntity.ok(userProfileService.saveProfilePicture(userId, file));
    }

    /**
     * Real file upload for the resume. Replaces the old flow where only the
     * filename string was sent - the PDF/DOCX bytes were never uploaded.
     */
    @PostMapping(value = "/me/resume", consumes = "multipart/form-data")
    public ResponseEntity<UserProfileDto> uploadResume(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("file") MultipartFile file) {
        String userId = resolveUserId(currentUser);
        return ResponseEntity.ok(userProfileService.saveResume(userId, file));
    }

    /**
     * Upload one or more certificate documents (in addition to the existing
     * text-only certification metadata form).
     */
    @PostMapping(value = "/me/certificates", consumes = "multipart/form-data")
    public ResponseEntity<UserProfileDto> uploadCertificateFiles(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("files") List<MultipartFile> files) {
        String userId = resolveUserId(currentUser);
        return ResponseEntity.ok(userProfileService.addCertificateFiles(userId, files));
    }
}
