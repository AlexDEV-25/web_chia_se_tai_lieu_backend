package com.example.app.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.app.constant.NotificationType;
import com.example.app.event.UserFollowCreateEvent;
import com.example.app.helper.NotificationHelper;
import com.example.app.model.User;
import com.example.app.service.NotificationService;
import com.example.app.service.UserNotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserFollowCreateListener implements NotificationHelper {
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
	public void handle(UserFollowCreateEvent event) {
		User follower = event.getFollower();
		User following = event.getFollowing();

		String link = frontendDomain + "/profile/" + follower.getId();
		String content = "người dùng \" " + follower.getUsername() + "\" đã theo dõi bạn";
		sendToAuthor(content, link, follower, following, NotificationType.INFO);

	}
}
