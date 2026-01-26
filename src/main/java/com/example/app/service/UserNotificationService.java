package com.example.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.UserNotificationRequest;
import com.example.app.dto.response.UserNotificationResponse;
import com.example.app.mapper.UserNotificationMapper;
import com.example.app.model.UserNotification;
import com.example.app.repository.UserNotificationRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserNotificationService {
	private final UserNotificationRepository userNotificationRepository;
	private final UserNotificationMapper userNotificationMapper;

	@PreAuthorize("hasAuthority('CREATE_USER_NOTIFICATION')")
	public UserNotificationResponse save(UserNotificationRequest request) {
		UserNotification userNotification = userNotificationMapper.requestToUserNotification(request);
		UserNotification saved = userNotificationRepository.save(userNotification);
		UserNotificationResponse response = userNotificationMapper.userNotificationToResponse(saved);
		return response;
	}

	@PreAuthorize("hasAuthority('GET_ALL_USER_NOTIFICATION')")
	public List<UserNotificationResponse> getByReceiver(Long receiverId) {
		List<UserNotification> userNotifications = userNotificationRepository.findByReceiverId(receiverId);
		List<UserNotificationResponse> response = new ArrayList<UserNotificationResponse>();
		for (UserNotification un : userNotifications) {
			response.add(userNotificationMapper.userNotificationToResponse(un));
		}
		return response;
	}

	@PreAuthorize("hasAuthority('GET_UNREAD_USER_NOTIFICATION')")
	public List<UserNotificationResponse> getByReceiverIdAndReadFalse(Long receiverId) {
		List<UserNotification> userNotifications = userNotificationRepository.findByReceiverIdAndReadFalse(receiverId);
		List<UserNotificationResponse> response = new ArrayList<UserNotificationResponse>();
		for (UserNotification un : userNotifications) {
			response.add(userNotificationMapper.userNotificationToResponse(un));
		}
		return response;
	}

	@PreAuthorize("hasRole('READ_NOTIFICATION')")
	public UserNotificationResponse read(Long id) {
		UserNotification entity = userNotificationRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));
		entity.setRead(true);
		UserNotification saved = userNotificationRepository.save(entity);
		return userNotificationMapper.userNotificationToResponse(saved);
	}
}
