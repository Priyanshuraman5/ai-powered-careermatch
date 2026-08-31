package com.careermatch.service;

import com.careermatch.model.dto.NotificationDtos.NotificationResponse;
import com.careermatch.model.entity.Notification;
import com.careermatch.model.entity.User;
import com.careermatch.exception.ApiException;
import com.careermatch.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void notify(User user, String title, String message, Notification.NotificationType type) {
        Notification notification = Notification.builder()
            .user(user).title(title).message(message).type(type).build();
        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public long unreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public NotificationResponse markRead(Long userId, Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
            .orElseThrow(() -> ApiException.notFound("Notification not found"));
        if (!n.getUser().getId().equals(userId)) {
            throw ApiException.unauthorized("Not your notification");
        }
        n.setRead(true);
        return toResponse(notificationRepository.save(n));
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
            n.getId(), n.getTitle(), n.getMessage(), n.getType().name(), n.isRead(), n.getCreatedAt()
        );
    }
}
