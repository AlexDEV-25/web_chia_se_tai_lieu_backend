package com.example.app.dto.response;

import com.example.app.share.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
	private Long id;
	private String content;
	private NotificationType type;
}
