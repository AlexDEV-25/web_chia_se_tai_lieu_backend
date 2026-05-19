package com.example.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.usernotificaion.UserNotificationResponse;
import com.example.app.service.UserNotificationService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/user-notifications")
@AllArgsConstructor
public class UserNotificationController {
	private final UserNotificationService userNotificationService;

	@GetMapping("/receiver")
	public APIResponse<UserNotificationResponse> getByReceiver() {
		APIResponse<UserNotificationResponse> apiResponse = new APIResponse<UserNotificationResponse>();
		apiResponse.setResultList(userNotificationService.getByReceiver());
		return apiResponse;
	}

	@GetMapping("/receiver/unread")
	public APIResponse<UserNotificationResponse> getByReceiverIdAndReadFalse() {
		APIResponse<UserNotificationResponse> apiResponse = new APIResponse<UserNotificationResponse>();
		apiResponse.setResultList(userNotificationService.getByReceiverIdAndReadFalse());
		return apiResponse;
	}

	@PutMapping("read/{id}")
	public APIResponse<UserNotificationResponse> read(@PathVariable Long id) {
		APIResponse<UserNotificationResponse> apiResponse = new APIResponse<UserNotificationResponse>();
		apiResponse.setResult(userNotificationService.read(id));
		return apiResponse;
	}

	@PutMapping("read-all/{id}")
	public APIResponse<Void> readAll(@PathVariable Long id) {
		userNotificationService.readAll(id);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		return apiResponse;
	}
}
