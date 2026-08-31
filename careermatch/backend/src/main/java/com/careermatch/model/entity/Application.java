package com.careermatch.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity

@Table(name = "applications", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "job_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id")
    private Job job;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    private Double matchScoreAtApply;

    @Builder.Default
    private LocalDateTime appliedAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    public enum ApplicationStatus { APPLIED, UNDER_REVIEW, INTERVIEW, OFFER, REJECTED, WITHDRAWN }
}
