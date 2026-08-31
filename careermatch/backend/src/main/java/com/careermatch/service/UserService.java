package com.careermatch.service;

import com.careermatch.exception.ApiException;
import com.careermatch.model.dto.UserDtos.*;
import com.careermatch.model.entity.Skill;
import com.careermatch.model.entity.User;
import com.careermatch.repository.SkillRepository;
import com.careermatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public User getById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> ApiException.notFound("User not found"));
    }

    public ProfileResponse getProfile(Long userId) {
        User user = getById(userId);
        return toProfileResponse(user);
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = getById(userId);

        if (request.fullName() != null) user.setFullName(request.fullName());
        if (request.headline() != null) user.setHeadline(request.headline());
        if (request.location() != null) user.setLocation(request.location());
        if (request.phone() != null) user.setPhone(request.phone());

        if (request.skills() != null) {
            Set<Skill> skills = request.skills().stream()
                .map(this::findOrCreateSkill)
                .collect(Collectors.toSet());
            user.setSkills(skills);
        }

        user = userRepository.save(user);
        return toProfileResponse(user);
    }

    @Transactional
    public Skill findOrCreateSkill(String name) {
        String trimmed = name.trim();
        return skillRepository.findByNameIgnoreCase(trimmed)
            .orElseGet(() -> skillRepository.save(Skill.builder().name(trimmed).category("Other").build()));
    }

    private ProfileResponse toProfileResponse(User user) {
        List<String> skillNames = user.getSkills().stream()
            .map(Skill::getName)
            .sorted()
            .collect(Collectors.toList());

        return new ProfileResponse(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getHeadline(),
            user.getLocation(),
            user.getPhone(),
            skillNames,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
    }
}
