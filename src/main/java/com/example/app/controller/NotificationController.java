package com.example.app.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.NotificationRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.notification.NotificationResponse;
import com.example.app.service.NotificationService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
public class NotificationController {
	private final NotificationService notificationService;

	@PostMapping
	public APIResponse<NotificationResponse> create(@RequestBody @Valid NotificationRequest dto) {
		APIResponse<NotificationResponse> apiResponse = new APIResponse<NotificationResponse>();
		apiResponse.setResult(notificationService.save(dto));
		return apiResponse;
	}

}
