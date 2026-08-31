package com.careermatch.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resumes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private String fileName;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String rawText;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String extractedSkillsCsv;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProcessingStatus status = ProcessingStatus.PENDING;

    @Builder.Default
    private LocalDateTime uploadedAt = LocalDateTime.now();

    public enum ProcessingStatus { PENDING, PROCESSING, PROCESSED, FAILED }
}