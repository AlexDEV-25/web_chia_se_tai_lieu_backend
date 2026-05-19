package com.example.app.controller.admin;

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
		APIResponse<DailyCountResponse> apiResponse = new APIResponse<DailyCountResponse>();
		apiResponse.setResultList(statisticsService.userLast7Days());
		return apiResponse;
	}

	@GetMapping("/documents/last-7-days")
	public APIResponse<DailyCountResponse> documentLast7Days() {
		APIResponse<DailyCountResponse> apiResponse = new APIResponse<DailyCountResponse>();
		apiResponse.setResultList(statisticsService.documentLast7Days());
		return apiResponse;
	}

	@GetMapping("/documents/by-category")
	public APIResponse<CategoryCountResponse> documentByCategory() {
		APIResponse<CategoryCountResponse> apiResponse = new APIResponse<CategoryCountResponse>();
		apiResponse.setResultList(statisticsService.documentByCategory());
		return apiResponse;
	}

	@GetMapping("/lessons/last-7-days")
	public APIResponse<DailyCountResponse> lessonLast7Days() {
		APIResponse<DailyCountResponse> apiResponse = new APIResponse<DailyCountResponse>();
		apiResponse.setResultList(statisticsService.lessonLast7Days());
		return apiResponse;
	}

	@GetMapping("/lessons/by-category")
	public APIResponse<CategoryCountResponse> lessonByCategory() {
		APIResponse<CategoryCountResponse> apiResponse = new APIResponse<CategoryCountResponse>();
		apiResponse.setResultList(statisticsService.lessonByCategory());
		return apiResponse;
	}
}
