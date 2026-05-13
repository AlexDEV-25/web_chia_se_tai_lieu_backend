package com.example.app.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.NotificationRequest;
import com.example.app.dto.response.notification.NotificationResponse;
import com.example.app.mapper.NotificationMapper;
import com.example.app.model.Notification;
import com.example.app.repository.NotificationRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotificationService {
	private final NotificationRepository notificationRepository;
	private final NotificationMapper notificationMapper;

	@PreAuthorize("hasRole('ADMIN')")
	public NotificationResponse save(NotificationRequest request) {
		Notification notification = notificationMapper.requestToNotification(request);
		Notification saved = notificationRepository.save(notification);
		NotificationResponse response = notificationMapper.notificationToResponse(saved);
		return response;
	}

	public Notification saveNotification(NotificationRequest request) {
		Notification notification = notificationMapper.requestToNotification(request);
		Notification saved = notificationRepository.save(notification);
		return saved;
	}

}
