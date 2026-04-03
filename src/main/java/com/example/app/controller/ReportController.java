package com.example.app.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.ReportRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.report.ReportUserResponse;
import com.example.app.service.ReportService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@AllArgsConstructor
public class ReportController {
	private final ReportService reportService;

	@PostMapping
	public APIResponse<ReportUserResponse> report(@RequestBody @Valid ReportRequest dto) {
		ReportUserResponse response = reportService.report(dto);
		APIResponse<ReportUserResponse> apiResponse = new APIResponse<ReportUserResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}
}
