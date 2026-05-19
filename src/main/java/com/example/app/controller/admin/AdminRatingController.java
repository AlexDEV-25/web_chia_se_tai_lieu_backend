package com.example.app.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.rating.RatingAdminResponse;
import com.example.app.dto.response.rating.RatingDetailAdminResponse;
import com.example.app.service.RatingService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/ratings/admin")
@AllArgsConstructor
public class AdminRatingController {
	private final RatingService ratingService;

	@GetMapping("/document/{docId}")
	public APIResponse<RatingDetailAdminResponse> getByDocument(@PathVariable Long docId) {
		APIResponse<RatingDetailAdminResponse> apiResponse = new APIResponse<RatingDetailAdminResponse>();
		apiResponse.setResult(ratingService.getByDocument(docId));
		return apiResponse;
	}

	@GetMapping("/lesson/{lessonId}")
	public APIResponse<RatingDetailAdminResponse> getByLesson(@PathVariable Long lessonId) {
		APIResponse<RatingDetailAdminResponse> apiResponse = new APIResponse<RatingDetailAdminResponse>();
		apiResponse.setResult(ratingService.getByLesson(lessonId));
		return apiResponse;
	}

	@GetMapping("/document")
	public APIResponse<RatingAdminResponse> getAllDocumentRatingSummary() {
		APIResponse<RatingAdminResponse> apiResponse = new APIResponse<RatingAdminResponse>();
		apiResponse.setResultList(ratingService.getAllDocumentRatingSummary());
		return apiResponse;
	}

	@GetMapping("/lesson")
	public APIResponse<RatingAdminResponse> getAllLessonRatingSummary() {
		APIResponse<RatingAdminResponse> apiResponse = new APIResponse<RatingAdminResponse>();
		apiResponse.setResultList(ratingService.getAllLessonRatingSummary());
		return apiResponse;
	}

}
