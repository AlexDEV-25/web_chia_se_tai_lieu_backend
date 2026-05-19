package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.constant.AppError;
import com.example.app.dto.request.UserNotificationRequest;
import com.example.app.dto.response.usernotificaion.UserNotificationResponse;
import com.example.app.exception.AppException;
import com.example.app.helper.GetUserByToken;
import com.example.app.mapper.UserNotificationMapper;
import com.example.app.model.Notification;
import com.example.app.model.User;
import com.example.app.model.UserFollow;
import com.example.app.model.UserNotification;
import com.example.app.repository.UserFollowRepository;
import com.example.app.repository.UserNotificationRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserNotificationService {
	private final UserNotificationRepository userNotificationRepository;
	private final UserFollowRepository userFollowRepository;
	private final UserNotificationMapper userNotificationMapper;
	private final GetUserByToken getUserByToken;

	@PreAuthorize("hasAuthority('GET_ALL_USER_NOTIFICATION')")
	public List<UserNotificationResponse> getByReceiver() {
		User receiver = getUserByToken.get();
		List<UserNotification> userNotifications = userNotificationRepository.findByReceiver_Id(receiver.getId());
		List<UserNotificationResponse> response = new ArrayList<UserNotificationResponse>();
		for (UserNotification un : userNotifications) {
			response.add(userNotificationMapper.userNotificationToResponse(un));
		}
		return response;
	}

	@PreAuthorize("hasAuthority('GET_UNREAD_USER_NOTIFICATION')")
	public List<UserNotificationResponse> getByReceiverIdAndReadFalse() {
		User receiver = getUserByToken.get();
		List<UserNotification> userNotifications = userNotificationRepository
				.findByReceiver_IdAndReadFalse(receiver.getId());
		List<UserNotificationResponse> response = new ArrayList<UserNotificationResponse>();
		for (UserNotification un : userNotifications) {
			response.add(userNotificationMapper.userNotificationToResponse(un));
		}
		return response;
	}

	@PreAuthorize("hasAuthority('READ_NOTIFICATION')")
	public UserNotificationResponse read(Long id) {
		User receiver = getUserByToken.get();
		UserNotification entity = userNotificationRepository.findByIdAndReceiver_IdAndReadFalse(id, receiver.getId())
				.orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));
		entity.setRead(true);
		UserNotification saved = userNotificationRepository.save(entity);
		return userNotificationMapper.userNotificationToResponse(saved);
	}

	@PreAuthorize("hasAuthority('READ_ALL_NOTIFICATION')")
	public void readAll(Long id) {
		List<UserNotification> entity = userNotificationRepository.findByReceiver_IdAndReadFalse(id);
		for (UserNotification userNotification : entity) {
			userNotification.setRead(true);
			userNotificationRepository.save(userNotification);
		}
	}

	public boolean saveUserNotification(UserNotificationRequest request) {
		UserNotification userNotification = userNotificationMapper.requestToUserNotification(request);
		userNotification.setCreatedAt(LocalDateTime.now());
		UserNotification saved = userNotificationRepository.save(userNotification);
		return (saved != null) ? true : false;
	}

	public void sendToFollower(User admin, Long authorId, Notification notificationToAudiences, String content,
			String link) {
		List<UserFollow> ListFollower = userFollowRepository.findByFollowing_Id(authorId);
		for (UserFollow uf : ListFollower) {
			UserNotificationRequest userNotificationRequestToAudiences = new UserNotificationRequest(admin,
					uf.getFollower(), notificationToAudiences, false);
			if (!saveUserNotification(userNotificationRequestToAudiences)) {
				throw AppException.builder().appError(AppError.CREATE_NOTIFICATION_FAILED).build();
			}
		}
	}
}
