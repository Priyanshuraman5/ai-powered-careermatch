package com.careermatch.model.dto;

import java.time.LocalDateTime;

public class ApplicationDtos {

    public record ApplicationResponse(
        Long id,
        Long jobId,
        String jobTitle,
        String company,
        String status,
        Double matchScoreAtApply,
        LocalDateTime appliedAt
    ) {}

    public record ApplyRequest(Long jobId) {}

    public record StatusUpdateRequest(String status) {}
}
