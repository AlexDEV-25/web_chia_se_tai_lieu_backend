package com.example.app.share;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.app.dto.request.NotificationRequest;
import com.example.app.dto.request.UserNotificationRequest;
import com.example.app.exception.AppException;
import com.example.app.mapper.NotificationMapper;
import com.example.app.mapper.UserNotificationMapper;
import com.example.app.model.Notification;
import com.example.app.model.User;
import com.example.app.model.UserNotification;
import com.example.app.repository.NotificationRepository;
import com.example.app.repository.UserNotificationRepository;
import com.example.app.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SendNotification {
	private final UserRepository userRepository;
	private final UserNotificationRepository userNotificationRepository;
	private final UserNotificationMapper userNotificationMapper;
	private final NotificationRepository notificationRepository;
	private final NotificationMapper notificationMapper;

	@Transactional
	public Long saveNotification(String content, NotificationType type) {
		NotificationRequest request = new NotificationRequest(content, type);
		Notification notification = notificationMapper.requestToNotification(request);
		Notification saved = notificationRepository.save(notification);
		return saved.getId();
	}

	@Transactional
	public boolean saveUserNotification(Long senderId, Long receiverId, Long notificationId) {
		UserNotificationRequest request = new UserNotificationRequest(senderId, receiverId, notificationId, false);
		UserNotification userNotification = userNotificationMapper.requestToUserNotification(request);

		User sender = senderId != null ? userRepository.findById(senderId)
				.orElseThrow(() -> new AppException("sender không tồn tại", 1001, HttpStatus.BAD_REQUEST)) : null;
		userNotification.setSender(sender);

		Notification notification = notificationId != null ? notificationRepository.findById(notificationId)
				.orElseThrow(() -> new AppException("notification không tồn tại", 1001, HttpStatus.BAD_REQUEST)) : null;
		userNotification.setNotification(notification);

		User receiver = receiverId != null ? userRepository.findById(receiverId)
				.orElseThrow(() -> new AppException("receiver không tồn tại", 1001, HttpStatus.BAD_REQUEST)) : null;
		userNotification.setReceiver(receiver);

		userNotification.setCreatedAt(LocalDateTime.now());
		UserNotification saved = userNotificationRepository.save(userNotification);
		return (saved != null) ? true : false;
	}
}
