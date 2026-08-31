package com.careermatch.service;

import com.careermatch.exception.ApiException;
import com.careermatch.matching.SemanticMatcher;
import com.careermatch.matching.SkillExtractor;
import com.careermatch.model.dto.ResumeDtos.ResumeResponse;
import com.careermatch.model.entity.Notification;
import com.careermatch.model.entity.Resume;
import com.careermatch.model.entity.Skill;
import com.careermatch.model.entity.User;
import com.careermatch.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeParsingService parsingService;
    private final SemanticMatcher semanticMatcher;
    private final SkillExtractor skillExtractor;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional
    public ResumeResponse uploadAndProcess(
            Long userId,
            MultipartFile file) {

        User user =
                userService.getById(userId);

        Resume resume = Resume.builder()
                .user(user)
                .fileName(file.getOriginalFilename())
                .status(Resume.ProcessingStatus.PROCESSING)
                .build();

        resume =
                resumeRepository.save(resume);

        try {

            String text =
                    parsingService.extractText(file);

            resume.setRawText(text);

            /*
             * Extract canonical skills using the
             * application-level SkillTaxonomy.
             *
             * SkillExtractor handles:
             * - canonical skill names
             * - aliases
             * - boundary-aware matching
             */
            Set<String> extractedSkills =
                    skillExtractor.extractSkills(text);

            resume.setExtractedSkillsCsv(
                    String.join(",", extractedSkills)
            );

            resume.setStatus(
                    Resume.ProcessingStatus.PROCESSED
            );

            resume =
                    resumeRepository.save(resume);

            /*
             * Synchronize extracted skills
             * with user's profile.
             */
            Set<Skill> updatedSkills =
                    user.getSkills();

            if (updatedSkills == null) {
                updatedSkills =
                        new java.util.HashSet<>();
            }

            for (String skillName :
                    extractedSkills) {

                updatedSkills.add(
                        userService.findOrCreateSkill(
                                skillName
                        )
                );
            }

            user.setSkills(updatedSkills);

            notificationService.notify(
                    user,
                    "Resume processed",
                    "We found "
                            + extractedSkills.size()
                            + " skills in your resume and updated your profile.",
                    Notification.NotificationType.INFO
            );

        } catch (IOException e) {

            resume.setStatus(
                    Resume.ProcessingStatus.FAILED
            );

            resumeRepository.save(resume);

            throw ApiException.badRequest(
                    "Could not process resume file: "
                            + e.getMessage()
            );
        }

        return toResponse(resume);
    }

    public List<ResumeResponse> getHistory(
            Long userId) {

        return resumeRepository
                .findByUserIdOrderByUploadedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ResumeResponse toResponse(
            Resume resume) {

        List<String> skills =
                resume.getExtractedSkillsCsv() == null
                        || resume.getExtractedSkillsCsv().isBlank()
                        ? List.of()
                        : Arrays.asList(
                                resume.getExtractedSkillsCsv()
                                        .split(",")
                        );

        return new ResumeResponse(
                resume.getId(),
                resume.getFileName(),
                resume.getStatus().name(),
                skills,
                resume.getUploadedAt()
        );
    }
}