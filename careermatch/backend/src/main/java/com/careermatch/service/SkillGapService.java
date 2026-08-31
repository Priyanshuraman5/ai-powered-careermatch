package com.careermatch.service;

import com.careermatch.matching.MatchResult;
import com.careermatch.matching.SemanticMatcher;
import com.careermatch.matching.SkillExtractor;
import com.careermatch.model.entity.Resume;
import com.careermatch.model.entity.Skill;
import com.careermatch.model.entity.User;
import com.careermatch.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillGapService {

    private final SemanticMatcher semanticMatcher;
    private final SkillExtractor skillExtractor;
    private final ResumeRepository resumeRepository;

    @Transactional(readOnly = true)
    public AnalysisResult analyze(
            User user,
            String jobDescription
    ) {

        if (user == null) {
            throw new IllegalArgumentException("User is required.");
        }

        if (jobDescription == null || jobDescription.isBlank()) {
            throw new IllegalArgumentException(
                    "Job description is required."
            );
        }

        // =========================================================
        // 1. Get latest uploaded resume
        // =========================================================

        Resume latestResume = resumeRepository
                .findByUserIdOrderByUploadedAtDesc(user.getId())
                .stream()
                .findFirst()
                .orElse(null);

        // =========================================================
        // 2. Build candidate text
        //
        // Resume text is the primary source.
        // Headline + profile skills are additional context.
        // =========================================================

        String candidateText =
                buildCandidateText(user, latestResume);

        // =========================================================
        // 3. Extract skills from candidate
        //
        // IMPORTANT:
        // Create a mutable Set because SkillExtractor may return
        // Set.of(), which is immutable.
        // =========================================================

        Set<String> candidateSkills =
                new LinkedHashSet<>(
                        Optional.ofNullable(
                                skillExtractor.extractSkills(candidateText)
                        ).orElseGet(Set::of)
                );

        // =========================================================
        // 4. Add skills already stored on user profile
        // =========================================================

        if (user.getSkills() != null) {

            user.getSkills()
                    .stream()
                    .map(Skill::getName)
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(skill -> !skill.isBlank())
                    .map(skill ->
                            skill.toLowerCase(Locale.ROOT)
                    )
                    .forEach(candidateSkills::add);
        }

        // =========================================================
        // 5. Extract required skills from job description
        //
        // Uses the SAME SkillExtractor / taxonomy.
        // This keeps resume and job skill extraction consistent.
        // =========================================================

        Set<String> requiredSkills =
                new LinkedHashSet<>(
                        Optional.ofNullable(
                                skillExtractor.extractSkills(jobDescription)
                        ).orElseGet(Set::of)
                );

        // =========================================================
        // 6. Perform matching
        //
        // Explicit skill matching + semantic similarity.
        // =========================================================

        MatchResult match = semanticMatcher.match(
                candidateText,
                candidateSkills,
                jobDescription,
                requiredSkills
        );

        // =========================================================
        // 7. Calculate scores
        // =========================================================

        int matchingScore =
                toPercentage(match.score());

        /*
         * ATS score:
         *
         * The matching score already contains:
         *   70% skill coverage
         *   30% semantic similarity
         *
         * We combine the final match score with semantic similarity
         * to produce a separate ATS-oriented indicator.
         */
        int atsScore =
                toPercentage(
                        (match.score()
                                + match.semanticSimilarity()) / 2.0
                );

        int profileStrength =
                toPercentage(
                        match.skillCoverage()
                );

        // =========================================================
        // 8. Build recommendations
        // =========================================================

        List<String> recommendations =
                buildRecommendations(
                        match.missingSkills()
                );

        // =========================================================
        // 9. Return result
        // =========================================================

        return new AnalysisResult(
                atsScore,
                matchingScore,
                profileStrength,
                match.matchedSkills(),
                match.missingSkills(),
                recommendations,
                match.skillCoverage(),
                match.semanticSimilarity()
        );
    }

    // =============================================================
    // Candidate Text
    // =============================================================

    /**
     * Builds the text used for semantic matching.
     *
     * Priority:
     *
     * 1. Latest resume raw text
     * 2. User headline
     * 3. Stored profile skills
     */
    private String buildCandidateText(
            User user,
            Resume latestResume
    ) {

        StringBuilder text =
                new StringBuilder();

        // ---------------------------------------------------------
        // Latest resume
        // ---------------------------------------------------------

        if (latestResume != null
                && latestResume.getRawText() != null
                && !latestResume.getRawText().isBlank()) {

            text.append(
                    latestResume.getRawText()
            ).append(" ");
        }

        // ---------------------------------------------------------
        // User headline
        // ---------------------------------------------------------

        if (user.getHeadline() != null
                && !user.getHeadline().isBlank()) {

            text.append(
                    user.getHeadline()
            ).append(" ");
        }

        // ---------------------------------------------------------
        // Profile skills
        // ---------------------------------------------------------

        if (user.getSkills() != null) {

            user.getSkills()
                    .stream()
                    .map(Skill::getName)
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(skill -> !skill.isBlank())
                    .forEach(skill ->
                            text.append(skill).append(" ")
                    );
        }

        return text.toString().trim();
    }

    // =============================================================
    // Recommendations
    // =============================================================

    private List<String> buildRecommendations(
            List<String> missingSkills
    ) {

        if (missingSkills == null
                || missingSkills.isEmpty()) {

            return List.of(
                    "Your current skills match the identified job requirements well.",
                    "Keep your resume updated with recent projects and achievements.",
                    "Continue strengthening your existing technical skills."
            );
        }

        return missingSkills.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(skill -> !skill.isBlank())
                .distinct()
                .limit(5)
                .map(skill ->
                        "Consider learning or strengthening "
                                + skill
                                + " to improve your match for this role."
                )
                .collect(Collectors.toList());
    }

    // =============================================================
    // Score Conversion
    // =============================================================

    private int toPercentage(double value) {

        if (Double.isNaN(value)
                || Double.isInfinite(value)) {

            return 0;
        }

        return (int) Math.round(
                Math.max(
                        0.0,
                        Math.min(1.0, value)
                ) * 100.0
        );
    }

    // =============================================================
    // Analysis Result
    // =============================================================

    public record AnalysisResult(
            int atsScore,
            int matchingScore,
            int profileStrength,
            List<String> matchedSkills,
            List<String> missingSkills,
            List<String> recommendations,
            double skillCoverage,
            double semanticSimilarity
    ) {
    }
}