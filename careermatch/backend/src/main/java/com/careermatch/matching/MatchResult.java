package com.careermatch.matching;

import java.util.List;

/**
 * Result of a semantic match between a candidate profile (resume/skills) and a job.
 */
public record MatchResult(
    double score,              // 0.0 - 1.0 overall match score
    List<String> matchedSkills,
    List<String> missingSkills,
    double skillCoverage,      // fraction of required skills the candidate has
    double semanticSimilarity  // free-text similarity between resume and job description
) {}
