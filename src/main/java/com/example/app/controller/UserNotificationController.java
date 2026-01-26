package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.UserNotificationRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.UserNotificationResponse;
import com.example.app.service.UserNotificationService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/user-notifications")
@AllArgsConstructor
public class UserNotificationController {
	private final UserNotificationService userNotificationService;

	@PostMapping
	public APIResponse<UserNotificationResponse> create(@RequestBody @Valid UserNotificationRequest dto) {
		UserNotificationResponse response = userNotificationService.save(dto);
		APIResponse<UserNotificationResponse> apiResponse = new APIResponse<UserNotificationResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@GetMapping("/receiver/{receiverId}")
	public APIResponse<UserNotificationResponse> getByReceiver(@PathVariable Long receiverId) {
		List<UserNotificationResponse> response = userNotificationService.getByReceiver(receiverId);
		APIResponse<UserNotificationResponse> apiResponse = new APIResponse<UserNotificationResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/unread/{receiverId}")
	public APIResponse<UserNotificationResponse> getByReceiverIdAndReadFalse(@PathVariable Long receiverId) {
		List<UserNotificationResponse> response = userNotificationService.getByReceiverIdAndReadFalse(receiverId);
		APIResponse<UserNotificationResponse> apiResponse = new APIResponse<UserNotificationResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PutMapping("read/{id}")
	public APIResponse<UserNotificationResponse> hide(@PathVariable Long id) {
		UserNotificationResponse response = userNotificationService.read(id);
		APIResponse<UserNotificationResponse> apiResponse = new APIResponse<UserNotificationResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("change success");
		return apiResponse;
	}
}
