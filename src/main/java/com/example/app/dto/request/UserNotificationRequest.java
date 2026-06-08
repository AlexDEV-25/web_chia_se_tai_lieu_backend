package com.example.app.dto.request;

import com.example.app.model.Notification;
import com.example.app.model.User;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotificationRequest {

	@NotNull(message = "senderId không được để trống")
	private User sender;

	@NotNull(message = "receiverId không được để trống")
	private User receiver;

	@NotNull(message = "notificationId không được để trống")
	private Notification notification;

	private boolean read;
}
