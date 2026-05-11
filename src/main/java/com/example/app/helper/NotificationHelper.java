package com.example.app.helper;

import org.springframework.http.HttpStatus;

import com.example.app.constant.NotificationType;
import com.example.app.dto.request.NotificationRequest;
import com.example.app.dto.request.UserNotificationRequest;
import com.example.app.exception.AppException;
import com.example.app.model.Notification;
import com.example.app.model.User;
import com.example.app.service.NotificationService;
import com.example.app.service.UserNotificationService;

public interface NotificationHelper {

	public abstract NotificationService notificationService();

	public abstract UserNotificationService userNotificationService();

	// method chung
	default Notification createNotification(String content, String link, NotificationType type) {
		NotificationRequest request = new NotificationRequest(content, link, type);
		return notificationService().saveNotification(request);
	}

	// sendToAuthor
	default void sendToAuthor(String content, String link, User admin, User receiver, NotificationType type) {
		Notification notification = createNotification(content, link, type);

		UserNotificationRequest req = new UserNotificationRequest(admin, receiver, notification, false);

		if (!userNotificationService().saveUserNotification(req)) {
			throw new AppException("Tạo thông báo thất bại", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	// sendToFollower
	default void sendToFollower(String content, String link, User admin, User receiver, NotificationType type) {
		Notification notification = createNotification(content, link, type);

		userNotificationService().sendToFollower(admin, receiver.getId(), notification, content, link);
	}
}
