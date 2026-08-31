package com.careermatch.model.dto;

import java.util.List;

public class DashboardDtos {

    public record DashboardSummary(
        int totalApplications,
        int interviewCount,
        int offerCount,
        double averageMatchScore,
        List<JobDtos.JobSummary> recommendedJobs,
        List<String> topMissingSkills,
        List<SkillGapItem> skillGapBreakdown
    ) {}

    public record SkillGapItem(
        String skill,
        int demandCount,
        boolean userHasSkill
    ) {}
}
