package com.example.app.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.ReportRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.ReportResponse;
import com.example.app.service.ReportService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/reports")
@AllArgsConstructor
@Slf4j
public class ReportController {
	private final ReportService reportService;

	@PostMapping("/document")
	public APIResponse<ReportResponse> documentReport(@RequestBody @Valid ReportRequest dto) {
		ReportResponse response = reportService.documentReport(dto);
		APIResponse<ReportResponse> apiResponse = new APIResponse<ReportResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@PostMapping("/lesson")
	public APIResponse<ReportResponse> lessonReport(@RequestBody @Valid ReportRequest dto) {
		ReportResponse response = reportService.lessonReport(dto);
		APIResponse<ReportResponse> apiResponse = new APIResponse<ReportResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@DeleteMapping("/document/{id}")
	public APIResponse<Void> unReportDocument(@PathVariable Long id) {
		reportService.unReportDocument(id);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	@DeleteMapping("/lesson/{id}")
	public APIResponse<Void> unReportLesson(@PathVariable Long id) {
		reportService.unReportLesson(id);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}
}
