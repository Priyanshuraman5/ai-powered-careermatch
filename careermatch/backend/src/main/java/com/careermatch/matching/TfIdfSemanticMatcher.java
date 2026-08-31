package com.careermatch.matching;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Default semantic matching implementation.
 *
 * Combines:
 *
 * 1. Explicit skill coverage
 * 2. Text similarity using term-frequency cosine similarity
 *
 * Skill extraction is handled separately by {@link SkillExtractor}.
 *
 * This class is responsible only for matching a candidate
 * against a job description.
 *
 * The implementation is intentionally lightweight and dependency-free.
 * It can later be replaced by an embeddings/vector-based implementation
 * without changing the callers of {@link SemanticMatcher}.
 */
@Component
public class TfIdfSemanticMatcher implements SemanticMatcher {

    /*
     * Explicit skill overlap is weighted more heavily because
     * technical skill matching is the strongest signal for
     * a career/job matching system.
     */
    private static final double SKILL_WEIGHT = 0.70;

    /*
     * Text similarity provides an additional contextual signal.
     */
    private static final double SEMANTIC_WEIGHT = 0.30;

    /*
     * Common English words that should not contribute strongly
     * to text similarity.
     */
    private static final Set<String> STOPWORDS = Set.of(
            "the",
            "and",
            "a",
            "an",
            "to",
            "of",
            "in",
            "for",
            "on",
            "with",
            "is",
            "are",
            "as",
            "at",
            "by",
            "or",
            "be",
            "this",
            "that",
            "we",
            "you",
            "will",
            "your",
            "our",
            "it",
            "from",
            "have",
            "has",
            "had",
            "role",
            "job",
            "team",
            "work",
            "working",
            "years",
            "year",
            "experience",
            "strong",
            "candidate",
            "looking",
            "required",
            "requirements",
            "responsibilities",
            "skills",
            "skill"
    );

    /*
     * Tokenizer used by the lightweight text-similarity engine.
     *
     * Supports technology names containing characters such as:
     *
     * Java
     * C++
     * C#
     * Node.js
     * Spring
     */
    private static final Pattern WORD_PATTERN =
            Pattern.compile("[a-zA-Z][a-zA-Z0-9+#.]*");

    /**
     * Calculates the overall match between a candidate and a job.
     *
     * Final score:
     *
     *      70% skill coverage
     *      +
     *      30% text similarity
     */
    @Override
    public MatchResult match(
            String candidateText,
            Set<String> candidateSkills,
            String jobText,
            Set<String> requiredSkills) {

        /*
         * Normalize both skill sets before comparison.
         */
        Set<String> normalizedCandidateSkills =
                normalize(candidateSkills);

        Set<String> normalizedRequiredSkills =
                normalize(requiredSkills);

        /*
         * Find skills that exist in both the candidate
         * and the job requirements.
         */
        List<String> matchedSkills =
                normalizedRequiredSkills.stream()
                        .filter(normalizedCandidateSkills::contains)
                        .sorted()
                        .collect(Collectors.toList());

        /*
         * Find skills required by the job but missing
         * from the candidate.
         */
        List<String> missingSkills =
                normalizedRequiredSkills.stream()
                        .filter(skill ->
                                !normalizedCandidateSkills.contains(skill))
                        .sorted()
                        .collect(Collectors.toList());

        /*
         * Calculate skill coverage.
         *
         * Example:
         *
         * Required skills = 5
         * Matched skills   = 4
         *
         * Coverage = 4 / 5 = 0.80
         */
        double skillCoverage;

        if (normalizedRequiredSkills.isEmpty()) {

            /*
             * If no skills were detected from the job description,
             * there is no explicit skill coverage signal.
             */
            skillCoverage = 0.0;

        } else {

            skillCoverage =
                    (double) matchedSkills.size()
                            / normalizedRequiredSkills.size();
        }

        /*
         * Calculate text similarity between the candidate's
         * resume/profile text and the job description.
         */
        double semanticSimilarity =
                cosineSimilarity(
                        tokenize(
                                candidateText == null
                                        ? ""
                                        : candidateText
                        ),
                        tokenize(
                                jobText == null
                                        ? ""
                                        : jobText
                        )
                );

        /*
         * Calculate final matching score.
         *
         * Skill coverage = 70%
         * Text similarity = 30%
         */
        double score =
                (SKILL_WEIGHT * skillCoverage)
                        +
                        (SEMANTIC_WEIGHT * semanticSimilarity);

        /*
         * Keep the score safely between 0.0 and 1.0.
         */
        score =
                Math.min(
                        1.0,
                        Math.max(0.0, score)
                );

        /*
         * Round to three decimal places.
         *
         * Example:
         *
         * 0.713847 -> 0.714
         */
        score =
                Math.round(score * 1000.0)
                        / 1000.0;

        return new MatchResult(
                score,
                matchedSkills,
                missingSkills,
                skillCoverage,
                semanticSimilarity
        );
    }

    /**
     * Normalizes a set of skills before comparison.
     *
     * Example:
     *
     * " Spring Boot " -> "spring boot"
     * "JAVA"          -> "java"
     */
    private Set<String> normalize(
            Set<String> skills) {

        if (skills == null
                || skills.isEmpty()) {

            return Set.of();
        }

        return skills.stream()
                .filter(Objects::nonNull)
                .map(this::normalizeSkillName)
                .filter(skill -> !skill.isBlank())
                .collect(
                        Collectors.toCollection(
                                LinkedHashSet::new
                        )
                );
    }

    /**
     * Normalizes an individual skill name.
     */
    private String normalizeSkillName(
            String skill) {

        if (skill == null) {
            return "";
        }

        return skill
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }

    /**
     * Converts text into a term-frequency map.
     *
     * Example:
     *
     * "Java Java Spring Boot"
     *
     * becomes approximately:
     *
     * java   -> 2
     * spring -> 1
     * boot   -> 1
     */
    private Map<String, Integer> tokenize(
            String text) {

        Map<String, Integer> termFrequency =
                new HashMap<>();

        if (text == null || text.isBlank()) {
            return termFrequency;
        }

        var matcher =
                WORD_PATTERN.matcher(
                        text.toLowerCase(Locale.ROOT)
                );

        while (matcher.find()) {

            String token =
                    matcher.group()
                            .toLowerCase(Locale.ROOT);

            /*
             * Ignore very short tokens and common English
             * stopwords.
             */
            if (token.length() < 2
                    || STOPWORDS.contains(token)) {

                continue;
            }

            termFrequency.merge(
                    token,
                    1,
                    Integer::sum
            );
        }

        return termFrequency;
    }

    /**
     * Calculates cosine similarity between two
     * term-frequency vectors.
     *
     * Result:
     *
     * 0.0 -> no meaningful textual overlap
     * 1.0 -> maximum similarity
     */
    private double cosineSimilarity(
            Map<String, Integer> a,
            Map<String, Integer> b) {

        if (a == null
                || b == null
                || a.isEmpty()
                || b.isEmpty()) {

            return 0.0;
        }

        /*
         * Build the combined vocabulary.
         */
        Set<String> vocabulary =
                new HashSet<>(a.keySet());

        vocabulary.addAll(
                b.keySet()
        );

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        /*
         * Calculate:
         *
         * dot product
         * vector A magnitude
         * vector B magnitude
         */
        for (String term :
                vocabulary) {

            int frequencyA =
                    a.getOrDefault(
                            term,
                            0
                    );

            int frequencyB =
                    b.getOrDefault(
                            term,
                            0
                    );

            dotProduct +=
                    (double) frequencyA
                            * frequencyB;

            normA +=
                    (double) frequencyA
                            * frequencyA;

            normB +=
                    (double) frequencyB
                            * frequencyB;
        }

        /*
         * Prevent division by zero.
         */
        if (normA == 0.0
                || normB == 0.0) {

            return 0.0;
        }

        double similarity =
                dotProduct /
                        (
                                Math.sqrt(normA)
                                        *
                                Math.sqrt(normB)
                        );

        /*
         * Protect against floating-point anomalies.
         */
        return Math.min(
                1.0,
                Math.max(0.0, similarity)
        );
    }
}