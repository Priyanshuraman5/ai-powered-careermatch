package com.careermatch.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "skills")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /** e.g. "Programming Language", "Framework", "Soft Skill", "Cloud/DevOps", "Data" */
    private String category;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Skill s)) return false;
        return name != null && name.equalsIgnoreCase(s.name);
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : name.toLowerCase().hashCode();
    }
}
