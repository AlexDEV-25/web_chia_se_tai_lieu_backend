package com.example.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.NotificationRequest;
import com.example.app.dto.response.NotificationResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.NotificationMapper;
import com.example.app.model.Notification;
import com.example.app.repository.NotificationRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotificationService {
	private final NotificationRepository notificationRepository;
	private final NotificationMapper notificationMapper;

	@PreAuthorize("hasAuthority('CREATE_NOTIFICATION')")
	public NotificationResponse save(NotificationRequest request) {
		Notification notification = notificationMapper.requestToNotification(request);
		Notification saved = notificationRepository.save(notification);
		NotificationResponse response = notificationMapper.notificationToResponse(saved);
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<NotificationResponse> getAllNotifications() {
		List<Notification> notifications = notificationRepository.findAll();
		List<NotificationResponse> responses = new ArrayList<NotificationResponse>();
		for (Notification c : notifications) {
			responses.add(notificationMapper.notificationToResponse(c));
		}
		return responses;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public void delete(Long id) {
		try {
			notificationRepository.deleteById(id);
		} catch (AppException e) {
			throw new AppException("không tìm thấy thông báo", 1001, HttpStatus.BAD_REQUEST);
		}
	}
}
