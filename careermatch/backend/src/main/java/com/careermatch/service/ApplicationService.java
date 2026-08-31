package com.careermatch.service;

import com.careermatch.exception.ApiException;
import com.careermatch.matching.MatchResult;
import com.careermatch.model.dto.ApplicationDtos.*;
import com.careermatch.model.entity.Application;
import com.careermatch.model.entity.Job;
import com.careermatch.model.entity.Notification;
import com.careermatch.model.entity.User;
import com.careermatch.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobService jobService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional
    public ApplicationResponse apply(Long userId, ApplyRequest request) {
        User user = userService.getById(userId);
        Job job = jobService.getEntity(request.jobId());

        applicationRepository.findByUserIdAndJobId(userId, job.getId()).ifPresent(a -> {
            throw ApiException.conflict("You have already applied to this job");
        });

        MatchResult match = jobService.computeMatch(user, job);

        Application application = Application.builder()
            .user(user).job(job)
            .status(Application.ApplicationStatus.APPLIED)
            .matchScoreAtApply(match.score())
            .build();

        application = applicationRepository.save(application);

        notificationService.notify(user, "Application submitted",
            "Your application to " + job.getTitle() + " at " + job.getCompany() + " was submitted.",
            Notification.NotificationType.APPLICATION_UPDATE);

        return toResponse(application);
    }

    public List<ApplicationResponse> getForUser(Long userId) {
        return applicationRepository.findByUserIdOrderByAppliedAtDesc(userId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationResponse updateStatus(Long userId, Long applicationId, StatusUpdateRequest request) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> ApiException.notFound("Application not found"));

        if (!application.getUser().getId().equals(userId)) {
            throw ApiException.unauthorized("Not your application");
        }

        Application.ApplicationStatus newStatus;
        try {
            newStatus = Application.ApplicationStatus.valueOf(request.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw ApiException.badRequest("Invalid status: " + request.status());
        }

        application.setStatus(newStatus);
        application.setUpdatedAt(LocalDateTime.now());
        application = applicationRepository.save(application);

        notificationService.notify(application.getUser(), "Application updated",
            "Your application to " + application.getJob().getTitle() + " is now " + newStatus.name() + ".",
            Notification.NotificationType.APPLICATION_UPDATE);

        return toResponse(application);
    }

    private ApplicationResponse toResponse(Application a) {
        return new ApplicationResponse(
            a.getId(), a.getJob().getId(), a.getJob().getTitle(), a.getJob().getCompany(),
            a.getStatus().name(), a.getMatchScoreAtApply(), a.getAppliedAt()
        );
    }
}
