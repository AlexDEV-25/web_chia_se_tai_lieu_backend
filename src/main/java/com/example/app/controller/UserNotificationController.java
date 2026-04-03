package com.example.app.controller;

import java.util.List;

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

//	@PostMapping
//	public APIResponse<UserNotificationResponse> create(@RequestBody @Valid UserNotificationRequest dto) {
//		UserNotificationResponse response = userNotificationService.save(dto);
//		APIResponse<UserNotificationResponse> apiResponse = new APIResponse<UserNotificationResponse>();
//		apiResponse.setResult(response);
//		apiResponse.setMessage("save success");
//		return apiResponse;
//	}

	@GetMapping("/receiver")
	public APIResponse<UserNotificationResponse> getByReceiver() {
		List<UserNotificationResponse> response = userNotificationService.getByReceiver();
		APIResponse<UserNotificationResponse> apiResponse = new APIResponse<UserNotificationResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/receiver/unread")
	public APIResponse<UserNotificationResponse> getByReceiverIdAndReadFalse() {
		List<UserNotificationResponse> response = userNotificationService.getByReceiverIdAndReadFalse();
		APIResponse<UserNotificationResponse> apiResponse = new APIResponse<UserNotificationResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PutMapping("read/{id}")
	public APIResponse<UserNotificationResponse> read(@PathVariable Long id) {
		UserNotificationResponse response = userNotificationService.read(id);
		APIResponse<UserNotificationResponse> apiResponse = new APIResponse<UserNotificationResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("change success");
		return apiResponse;
	}

	@PutMapping("read-all/{id}")
	public APIResponse<Void> readAll(@PathVariable Long id) {
		userNotificationService.readAll(id);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		apiResponse.setMessage("read all success");
		return apiResponse;
	}
}
