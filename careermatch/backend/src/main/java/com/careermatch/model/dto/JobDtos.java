package com.careermatch.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public class JobDtos {

    public record JobSummary(
        Long id,
        String title,
        String company,
        String location,
        String employmentType,
        String experienceLevel,
        Integer salaryMin,
        Integer salaryMax,
        LocalDateTime postedAt,
        Double matchScore
    ) {}

    public record JobDetail(
        Long id,
        String title,
        String company,
        String location,
        String employmentType,
        String experienceLevel,
        Integer salaryMin,
        Integer salaryMax,
        String description,
        List<String> requiredSkills,
        LocalDateTime postedAt,
        Double matchScore,
        List<String> matchedSkills,
        List<String> missingSkills
    ) {}

    public record JobSearchRequest(
        String keyword,
        String location,
        String employmentType,
        String experienceLevel,
        Integer minSalary
    ) {}
}
