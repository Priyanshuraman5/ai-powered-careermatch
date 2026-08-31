package com.careermatch.model.dto;

import java.time.LocalDateTime;

public class NotificationDtos {

    public record NotificationResponse(
        Long id,
        String title,
        String message,
        String type,
        boolean read,
        LocalDateTime createdAt
    ) {}
}
