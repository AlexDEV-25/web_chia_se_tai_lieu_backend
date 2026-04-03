package com.example.app.controller.admin;

import java.util.List;

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
		List<ReportDetailAdminResponse> response = reportService.findByDocumentId(documentId);
		APIResponse<ReportDetailAdminResponse> apiResponse = new APIResponse<ReportDetailAdminResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/lesson/{lessonId}")
	public APIResponse<ReportDetailAdminResponse> findByLessonId(@PathVariable Long lessonId) {
		List<ReportDetailAdminResponse> response = reportService.findByLessonId(lessonId);
		APIResponse<ReportDetailAdminResponse> apiResponse = new APIResponse<ReportDetailAdminResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/document")
	public APIResponse<ReportAdminResponse> getAllDocumentReportSummary() {
		List<ReportAdminResponse> response = reportService.getAllDocumentReportSummary();
		APIResponse<ReportAdminResponse> apiResponse = new APIResponse<ReportAdminResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/lesson")
	public APIResponse<ReportAdminResponse> getAllLessonReportSummary() {
		List<ReportAdminResponse> response = reportService.getAllLessonReportSummary();
		APIResponse<ReportAdminResponse> apiResponse = new APIResponse<ReportAdminResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}
}
