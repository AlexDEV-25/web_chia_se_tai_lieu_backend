package com.example.app.controller.admin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.statistic.CategoryCountResponse;
import com.example.app.dto.response.statistic.DailyCountResponse;
import com.example.app.service.StatisticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class AdminStatisticsController {

	private final StatisticsService statisticsService;

	@GetMapping("/users/last-7-days")
	public APIResponse<DailyCountResponse> userLast7Days() {
		List<DailyCountResponse> response = statisticsService.userLast7Days();
		APIResponse<DailyCountResponse> apiResponse = new APIResponse<DailyCountResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/documents/last-7-days")
	public APIResponse<DailyCountResponse> documentLast7Days() {
		List<DailyCountResponse> response = statisticsService.documentLast7Days();
		APIResponse<DailyCountResponse> apiResponse = new APIResponse<DailyCountResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/documents/by-category")
	public APIResponse<CategoryCountResponse> documentByCategory() {
		List<CategoryCountResponse> response = statisticsService.documentByCategory();
		APIResponse<CategoryCountResponse> apiResponse = new APIResponse<CategoryCountResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/lessons/last-7-days")
	public APIResponse<DailyCountResponse> lessonLast7Days() {
		List<DailyCountResponse> response = statisticsService.lessonLast7Days();
		APIResponse<DailyCountResponse> apiResponse = new APIResponse<DailyCountResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/lessons/by-category")
	public APIResponse<CategoryCountResponse> lessonByCategory() {
		List<CategoryCountResponse> response = statisticsService.lessonByCategory();
		APIResponse<CategoryCountResponse> apiResponse = new APIResponse<CategoryCountResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}
}
