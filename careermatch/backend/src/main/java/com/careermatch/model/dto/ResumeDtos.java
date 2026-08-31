package com.careermatch.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ResumeDtos {

    public record ResumeResponse(
        Long id,
        String fileName,
        String status,
        List<String> extractedSkills,
        LocalDateTime uploadedAt
    ) {}
}
