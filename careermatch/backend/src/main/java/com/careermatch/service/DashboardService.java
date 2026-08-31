package com.careermatch.service;

import com.careermatch.matching.MatchResult;
import com.careermatch.model.dto.DashboardDtos.*;
import com.careermatch.model.dto.JobDtos.JobSummary;
import com.careermatch.model.entity.Application;
import com.careermatch.model.entity.Job;
import com.careermatch.model.entity.Skill;
import com.careermatch.model.entity.User;
import com.careermatch.repository.ApplicationRepository;
import com.careermatch.service.SerpApiJobService;
import com.careermatch.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Aggregates data from Job, Application, and User/Skill domains into a single
 * dashboard payload: recommended jobs, application funnel stats, and a
 * skill-gap breakdown (which skills are most in demand across open jobs that
 * the user doesn't yet have).
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final JobService jobService;
    private final UserService userService;
    private final SerpApiJobService serpApiJobService;

    private static final int TOP_RECOMMENDATIONS = 5;
    private static final int TOP_MISSING_SKILLS = 8;

    public DashboardSummary getSummary(Long userId) {
        User user = userService.getById(userId);
        List<Application> applications = applicationRepository.findByUserIdOrderByAppliedAtDesc(userId);
        List<Job> activeJobs = jobRepository.findByActiveTrue();

        int interviewCount = (int) applications.stream()
                .filter(a -> a.getStatus() == Application.ApplicationStatus.INTERVIEW).count();
        int offerCount = (int) applications.stream()
                .filter(a -> a.getStatus() == Application.ApplicationStatus.OFFER).count();

        double avgMatch = applications.stream()
                .filter(a -> a.getMatchScoreAtApply() != null)
                .mapToDouble(Application::getMatchScoreAtApply)
                .average().orElse(0.0);

        // recommended jobs: highest match score among jobs not yet applied to
        Set<Long> appliedJobIds = applications.stream().map(a -> a.getJob().getId()).collect(Collectors.toSet());

        List<JobSummary> recommended = activeJobs.stream()
                .filter(j -> !appliedJobIds.contains(j.getId()))
                .map(job -> {
                    MatchResult match = jobService.computeMatch(user, job);
                    return new JobSummary(
                            job.getId(), job.getTitle(), job.getCompany(), job.getLocation(),
                            job.getEmploymentType().name(), job.getExperienceLevel(),
                            job.getSalaryMin(), job.getSalaryMax(), job.getPostedAt(), match.score()
                    );
                })
                .sorted(Comparator.comparing(JobSummary::matchScore, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(TOP_RECOMMENDATIONS)
                .collect(Collectors.toList());

        // skill-gap breakdown: demand count for every skill across active jobs vs. whether user has it
        Set<String> userSkills = user.getSkills().stream().map(Skill::getName).collect(Collectors.toSet());
        Map<String, Long> demandCounts = activeJobs.stream()
                .flatMap(j -> j.getRequiredSkills().stream())
                .map(Skill::getName)
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

        List<SkillGapItem> skillGap = demandCounts.entrySet().stream()
                .map(e -> new SkillGapItem(e.getKey(), e.getValue().intValue(), userSkills.contains(e.getKey())))
                .sorted(Comparator.comparingInt(SkillGapItem::demandCount).reversed())
                .collect(Collectors.toList());

        List<String> topMissing = skillGap.stream()
                .filter(item -> !item.userHasSkill())
                .map(SkillGapItem::skill)
                .limit(TOP_MISSING_SKILLS)
                .collect(Collectors.toList());

        return new DashboardSummary(
                applications.size(), interviewCount, offerCount,
                Math.round(avgMatch * 1000.0) / 1000.0,
                recommended, topMissing, skillGap
        );
    }
}
