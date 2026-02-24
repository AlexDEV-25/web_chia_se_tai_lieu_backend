package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
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

	@GetMapping("/document/{documentId}")
	public APIResponse<ReportResponse> findByDocumentId(@PathVariable Long documentId) {
		List<ReportResponse> response = reportService.findByDocumentId(documentId);
		APIResponse<ReportResponse> apiResponse = new APIResponse<ReportResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/lesson/{lessonId}")
	public APIResponse<ReportResponse> findByLessonId(@PathVariable Long lessonId) {
		List<ReportResponse> response = reportService.findByLessonId(lessonId);
		APIResponse<ReportResponse> apiResponse = new APIResponse<ReportResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}
}
