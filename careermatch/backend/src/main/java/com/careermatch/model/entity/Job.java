package com.careermatch.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "jobs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String company;

    private String location;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EmploymentType employmentType = EmploymentType.FULL_TIME;

    @Lob
    @Column(columnDefinition = "TEXT") // Changed from CLOB to TEXT for MySQL compatibility
    private String description;

    private Integer salaryMin;
    private Integer salaryMax;

    private String experienceLevel; // ENTRY, MID, SENIOR, LEAD

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "job_skills",
        joinColumns = @JoinColumn(name = "job_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> requiredSkills = new HashSet<>();

    @Builder.Default
    private LocalDateTime postedAt = LocalDateTime.now();

    @Builder.Default
    private boolean active = true;

    public enum EmploymentType { FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, REMOTE }
}