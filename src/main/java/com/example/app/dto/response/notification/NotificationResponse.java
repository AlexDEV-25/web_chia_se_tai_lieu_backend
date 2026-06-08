package com.example.app.dto.response.notification;

import com.example.app.constant.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
	private Long id;
	private String content;
	private String link;
	private NotificationType type;
}
