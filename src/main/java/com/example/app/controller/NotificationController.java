package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.NotificationResponse;
import com.example.app.service.NotificationService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
public class NotificationController {
	private final NotificationService notificationService;

//	@PostMapping
//	public APIResponse<NotificationResponse> create(@RequestBody @Valid NotificationRequest dto) {
//		NotificationResponse response = notificationService.save(dto);
//		APIResponse<NotificationResponse> apiResponse = new APIResponse<NotificationResponse>();
//		apiResponse.setResult(response);
//		apiResponse.setMessage("save success");
//		return apiResponse;
//	}

	@GetMapping
	public APIResponse<NotificationResponse> getAll() {
		List<NotificationResponse> response = notificationService.getAllNotifications();
		APIResponse<NotificationResponse> apiResponse = new APIResponse<NotificationResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@DeleteMapping("/{id}")
	public APIResponse<NotificationResponse> delete(@PathVariable Long id) {
		notificationService.delete(id);
		APIResponse<NotificationResponse> apiResponse = new APIResponse<NotificationResponse>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}
}
