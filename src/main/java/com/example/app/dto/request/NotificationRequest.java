package com.example.app.dto.request;

import com.example.app.constant.NotificationType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {

	@NotBlank(message = "content không được để trống")
	private String content;

	private String link;

	@NotNull(message = "Type không được null")
	private NotificationType type;
}
