package com.careermatch.repository;

import com.careermatch.model.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByActiveTrue();

    @Query("""
        SELECT j FROM Job j WHERE j.active = true
        AND (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(j.company) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR EXISTS (
                 SELECT 1 FROM j.requiredSkills skill
                 WHERE LOWER(skill.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
             ))
        AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
        AND (:employmentType IS NULL OR j.employmentType = :employmentType)
        AND (:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel)
        AND (:minSalary IS NULL OR j.salaryMax >= :minSalary)
        """)
    List<Job> search(String keyword, String location,
                      Job.EmploymentType employmentType, String experienceLevel, Integer minSalary);
}
