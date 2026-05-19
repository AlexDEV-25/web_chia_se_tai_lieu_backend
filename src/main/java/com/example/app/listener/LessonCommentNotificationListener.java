package com.example.app.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.app.constant.NotificationType;
import com.example.app.event.LessonCommentCreatedEvent;
import com.example.app.helper.NotificationHelper;
import com.example.app.model.User;
import com.example.app.service.NotificationService;
import com.example.app.service.UserNotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LessonCommentNotificationListener implements NotificationHelper {
	private final NotificationService notificationService;
	private final UserNotificationService userNotificationService;

	@Value("${app.domain.frontend}")
	private String frontendDomain;

	@Override
	public NotificationService notificationService() {
		return notificationService;
	}

	@Override
	public UserNotificationService userNotificationService() {
		return userNotificationService;
	}

	@EventListener
	public void handle(LessonCommentCreatedEvent event) {
		User sender = event.getComment().getUser();
		User receiver = event.getParentComment().getUser();

		String content = "người dùng \" " + sender.getUsername() + "\" đã trở lời bình luận của bạn";
		String link = frontendDomain + "/lesson/" + event.getComment().getLesson().getId();

		sendToAuthor(content, link, sender, receiver, NotificationType.INFO);
	}
}
