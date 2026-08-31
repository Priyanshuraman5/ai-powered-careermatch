package com.careermatch.repository;

import com.careermatch.model.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUserIdOrderByAppliedAtDesc(Long userId);
    Optional<Application> findByUserIdAndJobId(Long userId, Long jobId);
    long countByUserIdAndStatus(Long userId, Application.ApplicationStatus status);
}
