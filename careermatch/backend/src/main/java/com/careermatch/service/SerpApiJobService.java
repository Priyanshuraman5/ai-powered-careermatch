package com.careermatch.service;

import com.careermatch.exception.ApiException;
import com.careermatch.matching.MatchResult;
import com.careermatch.matching.SemanticMatcher;
import com.careermatch.model.entity.Skill;
import com.careermatch.model.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SerpApiJobService {

    @Value("${serpapi.key}")
    private String apiKey;

    @Value("${serpapi.base-url:https://serpapi.com/search}")
    private String baseUrl;

    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final SemanticMatcher semanticMatcher;

    public SerpApiJobService(
            ObjectMapper objectMapper,
            SemanticMatcher semanticMatcher
    ) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.create();
        this.semanticMatcher = semanticMatcher;
    }

    /**
     * Search live jobs from SerpAPI.
     *
     * Empty/no-result responses are returned as an empty list so that
     * recommendFor() can try broader queries.
     */
    public List<ExternalJob> search(String keyword, String location) {

        if (keyword == null || keyword.isBlank()) {
            return new ArrayList<>();
        }

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("engine", "google_jobs")
                .queryParam("q", keyword)
                .queryParam("gl", "in")
                .queryParam("hl", "en")
                .queryParam("api_key", apiKey);

        if (location != null && !location.isBlank()) {
            builder.queryParam("location", location);
        }

        String response;

        try {
            response = restClient.get()
                    .uri(builder.build().toUri())
                    .retrieve()
                    .body(String.class);

        } catch (RuntimeException e) {
            throw ApiException.badRequest(
                    "SerpAPI request failed. Check your API key, quota, and internet connection."
            );
        }

        JsonNode root;

        try {
            root = objectMapper.readTree(response);

        } catch (JsonProcessingException e) {
            throw ApiException.badRequest(
                    "SerpAPI returned an invalid response. Please try again later."
            );
        }

        /*
         * IMPORTANT:
         *
         * Google Jobs can return:
         *
         * "Google hasn't returned any results for this query."
         *
         * This is NOT an API-key failure.
         *
         * Return an empty list so recommendFor() can try
         * another broader query.
         */
        if (root.has("error")) {

            String error = root.get("error").asText();

            String normalizedError = error.toLowerCase();

            if (normalizedError.contains("hasn't returned any results")
                    || normalizedError.contains("no results")
                    || normalizedError.contains("did not return any results")) {

                return new ArrayList<>();
            }

            throw ApiException.badRequest(
                    "SerpAPI: " + error
            );
        }

        List<ExternalJob> jobs = new ArrayList<>();

        JsonNode jobsResults = root.path("jobs_results");

        if (jobsResults.isArray()) {

            for (JsonNode job : jobsResults) {

                jobs.add(
                        new ExternalJob(
                                getText(job, "title"),
                                getText(job, "company_name"),
                                getText(job, "location"),
                                getText(job, "via"),
                                getText(job, "description"),
                                getDirectApplyUrl(job)
                        )
                );
            }
        }

        return jobs;
    }

    /**
     * Default live recommendations.
     *
     * The user does NOT need to search.
     *
     * Flow:
     *
     * Dashboard
     *    ↓
     * recommendFor(user)
     *    ↓
     * User skills
     *    ↓
     * SerpAPI live jobs
     *    ↓
     * Existing SemanticMatcher
     *    ↓
     * Sort by match score
     *    ↓
     * Top 5
     */
    public List<ExternalJob> recommendFor(User user) {

        List<String> skills = user.getSkills().stream()
                .map(Skill::getName)
                .filter(skill -> skill != null && !skill.isBlank())
                .map(String::trim)
                .distinct()
                .limit(8)
                .collect(Collectors.toList());

        /*
         * Candidate text used by the existing SemanticMatcher.
         */
        String candidateText = buildCandidateText(user, skills);

        /*
         * If the user has skills, use those skills as the
         * default live-job query.
         *
         * Example:
         *
         * Java Spring Boot MySQL React
         *
         * We intentionally DO NOT append "Developer".
         */
        String skillQuery = String.join(" ", skills);

        List<ExternalJob> jobs = new ArrayList<>();

        /*
         * 1. Personalized search with user's location.
         */
        if (!skillQuery.isBlank()) {

            jobs = search(
                    skillQuery,
                    user.getLocation()
            );
        }

        /*
         * 2. Same personalized search without location.
         *
         * This is important because a location filter can make
         * Google Jobs return zero results.
         */
        if (jobs.isEmpty() && !skillQuery.isBlank()) {

            jobs = search(
                    skillQuery,
                    null
            );
        }

        /*
         * 3. Broader software-development search with location.
         */
        if (jobs.isEmpty()) {

            jobs = search(
                    "Software Developer",
                    user.getLocation()
            );
        }

        /*
         * 4. Final broad search without location.
         */
        if (jobs.isEmpty()) {

            jobs = search(
                    "Software Developer",
                    null
            );
        }

        /*
         * No duplicate/local keyword matcher.
         *
         * Every live SerpAPI job is scored by the EXISTING
         * SemanticMatcher implementation.
         */
        return jobs.stream()
                .sorted(
                        Comparator.comparingDouble(
                                (ExternalJob job) ->
                                        calculateSemanticScore(
                                                candidateText,
                                                skills,
                                                job
                                        )
                        ).reversed()
                )
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Build the candidate text used by SemanticMatcher.
     */
    private String buildCandidateText(
            User user,
            List<String> skills
    ) {

        StringBuilder text = new StringBuilder();

        if (user.getHeadline() != null
                && !user.getHeadline().isBlank()) {

            text.append(user.getHeadline()).append(" ");
        }

        text.append(String.join(" ", skills));

        return text.toString().trim();
    }

    /**
     * Use the project's existing SemanticMatcher.
     *
     * SerpAPI does not give us a controlled required-skills
     * collection, so the complete live job text is passed to
     * the matcher.
     */
    private double calculateSemanticScore(
            String candidateText,
            List<String> skills,
            ExternalJob job
    ) {

        Set<String> candidateSkills = new HashSet<>(skills);

        String jobText =
                safeText(job.title()) + " " +
                safeText(job.description()) + " " +
                safeText(job.company()) + " " +
                safeText(job.location());

        Set<String> requiredSkills = new HashSet<>();

        MatchResult result = semanticMatcher.match(
                candidateText,
                candidateSkills,
                jobText,
                requiredSkills
        );

        return result.score();
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    /**
     * Extract a text field safely from SerpAPI JSON.
     */
    private String getText(JsonNode node, String field) {

        JsonNode value = node.get(field);

        if (value == null || value.isNull()) {
            return null;
        }

        return value.asText();
    }

    /**
     * Get the actual application URL.
     */
    private String getDirectApplyUrl(JsonNode job) {

        JsonNode applyOptions = job.path("apply_options");

        if (applyOptions.isArray()) {

            for (JsonNode option : applyOptions) {

                String link = getText(option, "link");

                if (link != null && !link.isBlank()) {
                    return link;
                }
            }
        }

        return null;
    }

    /**
     * Object returned to the frontend.
     */
    public record ExternalJob(
            String title,
            String company,
            String location,
            String via,
            String description,
            String applyUrl
    ) {
    }
}