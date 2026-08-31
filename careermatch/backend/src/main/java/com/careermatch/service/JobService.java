package com.careermatch.service;

import com.careermatch.exception.ApiException;
import com.careermatch.matching.MatchResult;
import com.careermatch.matching.SemanticMatcher;
import com.careermatch.model.dto.JobDtos.*;
import com.careermatch.model.entity.Job;
import com.careermatch.model.entity.Skill;
import com.careermatch.model.entity.User;
import com.careermatch.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final SemanticMatcher semanticMatcher;
    private final UserService userService;

    public List<JobSummary> search(JobSearchRequest request, Long requestingUserId) {
        Job.EmploymentType employmentType = null;
        if (request.employmentType() != null && !request.employmentType().isBlank()) {
            try {
                employmentType = Job.EmploymentType.valueOf(request.employmentType().toUpperCase());
            } catch (IllegalArgumentException ignored) { /* ignore invalid filter */ }
        }

        List<Job> jobs = jobRepository.search(
            blankToNull(request.keyword()),
            blankToNull(request.location()),
            employmentType,
            blankToNull(request.experienceLevel()),
            request.minSalary()
        );

        User user = requestingUserId != null ? userService.getById(requestingUserId) : null;

        return jobs.stream()
            .map(job -> toSummary(job, user))
            .sorted(Comparator.comparing(JobSummary::matchScore, Comparator.nullsLast(Comparator.reverseOrder())))
            .collect(Collectors.toList());
    }

    public List<JobSummary> listAll(Long requestingUserId) {
        User user = requestingUserId != null ? userService.getById(requestingUserId) : null;
        return jobRepository.findByActiveTrue().stream()
            .map(job -> toSummary(job, user))
            .sorted(Comparator.comparing(JobSummary::matchScore, Comparator.nullsLast(Comparator.reverseOrder())))
            .collect(Collectors.toList());
    }

    public JobDetail getDetail(Long jobId, Long requestingUserId) {
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> ApiException.notFound("Job not found"));

        User user = requestingUserId != null ? userService.getById(requestingUserId) : null;

        List<String> requiredSkillNames = job.getRequiredSkills().stream()
            .map(Skill::getName).sorted().collect(Collectors.toList());

        if (user == null) {
            return new JobDetail(
                job.getId(), job.getTitle(), job.getCompany(), job.getLocation(),
                job.getEmploymentType().name(), job.getExperienceLevel(),
                job.getSalaryMin(), job.getSalaryMax(), job.getDescription(),
                requiredSkillNames, job.getPostedAt(), null, List.of(), List.of()
            );
        }

        MatchResult match = computeMatch(user, job);
        return new JobDetail(
            job.getId(), job.getTitle(), job.getCompany(), job.getLocation(),
            job.getEmploymentType().name(), job.getExperienceLevel(),
            job.getSalaryMin(), job.getSalaryMax(), job.getDescription(),
            requiredSkillNames, job.getPostedAt(), match.score(),
            match.matchedSkills(), match.missingSkills()
        );
    }

    public MatchResult computeMatch(User user, Job job) {
        Set<String> candidateSkills = user.getSkills().stream().map(Skill::getName).collect(Collectors.toSet());
        Set<String> requiredSkills = job.getRequiredSkills().stream().map(Skill::getName).collect(Collectors.toSet());

        String candidateText = (user.getHeadline() == null ? "" : user.getHeadline() + " ") + candidateSkillsText(candidateSkills);
        String jobText = (job.getTitle() == null ? "" : job.getTitle() + " ") + (job.getDescription() == null ? "" : job.getDescription());

        return semanticMatcher.match(candidateText, candidateSkills, jobText, requiredSkills);
    }

    Job getEntity(Long jobId) {
        return jobRepository.findById(jobId).orElseThrow(() -> ApiException.notFound("Job not found"));
    }

    private String candidateSkillsText(Set<String> skills) {
        return String.join(" ", skills);
    }

    private JobSummary toSummary(Job job, User user) {
        Double score = user == null ? null : computeMatch(user, job).score();
        return new JobSummary(
            job.getId(), job.getTitle(), job.getCompany(), job.getLocation(),
            job.getEmploymentType().name(), job.getExperienceLevel(),
            job.getSalaryMin(), job.getSalaryMax(), job.getPostedAt(), score
        );
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
