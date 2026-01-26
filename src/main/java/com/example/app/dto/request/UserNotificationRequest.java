package com.example.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationRequest {

	@NotNull(message = "senderId không được để trống")
	private Long senderId;

	@NotNull(message = "receiverId không được để trống")
	private Long receiverId;

	@NotNull(message = "notificationId không được để trống")
	private Long notificationId;

	private boolean read;
}
