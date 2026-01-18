package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.response.CategoryCountResponse;
import com.example.app.dto.response.DailyCountResponse;
import com.example.app.service.StatisticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class StatisticsController {

	private final StatisticsService statisticsService;

	@GetMapping("/users/last-7-days")
	public List<DailyCountResponse> userLast7Days() {
		return statisticsService.userLast7Days();
	}

	@GetMapping("/documents/last-7-days")
	public List<DailyCountResponse> documentLast7Days() {
		return statisticsService.documentLast7Days();
	}

	@GetMapping("/documents/by-category")
	public List<CategoryCountResponse> documentByCategory() {
		return statisticsService.documentByCategory();
	}

	@GetMapping("/lessons/last-7-days")
	public List<DailyCountResponse> lessonLast7Days() {
		return statisticsService.lessonLast7Days();
	}

	@GetMapping("/lessons/by-category")
	public List<CategoryCountResponse> lessonByCategory() {
		return statisticsService.lessonByCategory();
	}
}
