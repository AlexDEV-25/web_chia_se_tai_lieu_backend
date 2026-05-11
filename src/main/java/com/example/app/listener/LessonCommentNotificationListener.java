package com.example.app.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.app.constant.NotificationType;
import com.example.app.dto.request.NotificationRequest;
import com.example.app.dto.request.UserNotificationRequest;
import com.example.app.event.LessonCommentCreatedEvent;
import com.example.app.exception.AppException;
import com.example.app.model.Notification;
import com.example.app.model.User;
import com.example.app.service.NotificationService;
import com.example.app.service.UserNotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LessonCommentNotificationListener {
	private final NotificationService notificationService;
	private final UserNotificationService userNotificationService;

	@Value("${app.domain.frontend}")
	private String frontendDomain;

	@EventListener
	public void handle(LessonCommentCreatedEvent event) {
		User sender = event.getComment().getUser();
		User receiver = event.getParentComment().getUser();

		String content = "người dùng \" " + sender.getUsername() + "\" đã trở lời bình luận của bạn";
		String link = frontendDomain + "/lesson/" + event.getComment().getLesson().getId();

		NotificationRequest notificationRequest = new NotificationRequest(content, link, NotificationType.INFO);
		Notification notification = notificationService.saveNotification(notificationRequest);

		UserNotificationRequest userNotificationRequest = new UserNotificationRequest(sender, receiver, notification,
				false);
		if (!userNotificationService.saveUserNotification(userNotificationRequest)) {
			throw new AppException("Tạo thông báo thất bại", 1001, HttpStatus.BAD_REQUEST);
		}
	}
}
