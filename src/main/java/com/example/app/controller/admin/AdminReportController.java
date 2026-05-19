package com.example.app.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.report.ReportAdminResponse;
import com.example.app.dto.response.report.ReportDetailAdminResponse;
import com.example.app.service.ReportService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/reports/admin")
@AllArgsConstructor
public class AdminReportController {
	private final ReportService reportService;

	@GetMapping("/document/{documentId}")
	public APIResponse<ReportDetailAdminResponse> findByDocumentId(@PathVariable Long documentId) {
		APIResponse<ReportDetailAdminResponse> apiResponse = new APIResponse<ReportDetailAdminResponse>();
		apiResponse.setResultList(reportService.findByDocumentId(documentId));
		return apiResponse;
	}

	@GetMapping("/lesson/{lessonId}")
	public APIResponse<ReportDetailAdminResponse> findByLessonId(@PathVariable Long lessonId) {
		APIResponse<ReportDetailAdminResponse> apiResponse = new APIResponse<ReportDetailAdminResponse>();
		apiResponse.setResultList(reportService.findByLessonId(lessonId));
		return apiResponse;
	}

	@GetMapping("/document")
	public APIResponse<ReportAdminResponse> getAllDocumentReportSummary() {
		APIResponse<ReportAdminResponse> apiResponse = new APIResponse<ReportAdminResponse>();
		apiResponse.setResultList(reportService.getAllDocumentReportSummary());
		return apiResponse;
	}

	@GetMapping("/lesson")
	public APIResponse<ReportAdminResponse> getAllLessonReportSummary() {
		APIResponse<ReportAdminResponse> apiResponse = new APIResponse<ReportAdminResponse>();
		apiResponse.setResultList(reportService.getAllLessonReportSummary());
		return apiResponse;
	}
}
