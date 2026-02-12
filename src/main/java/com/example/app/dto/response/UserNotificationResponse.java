package com.example.app.dto.response;

import java.time.LocalDateTime;

import com.example.app.share.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationResponse {
	private Long id;
	private Long senderId;
	private String senderName;
	private Long receiverId;
	private String receiverName;
	private Long notificationId;
	private String notificationContent;
	private String notificationLink;
	private NotificationType notificationType;
	private boolean read;
	private LocalDateTime createdAt;
}
