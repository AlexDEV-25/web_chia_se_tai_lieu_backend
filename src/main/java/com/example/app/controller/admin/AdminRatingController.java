package com.example.app.controller.admin;

import java.util.List;

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
		RatingDetailAdminResponse response = ratingService.getByDocument(docId);
		APIResponse<RatingDetailAdminResponse> apiResponse = new APIResponse<RatingDetailAdminResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/lesson/{lessonId}")
	public APIResponse<RatingDetailAdminResponse> getByLesson(@PathVariable Long lessonId) {
		RatingDetailAdminResponse response = ratingService.getByLesson(lessonId);
		APIResponse<RatingDetailAdminResponse> apiResponse = new APIResponse<RatingDetailAdminResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/document")
	public APIResponse<RatingAdminResponse> getAllDocumentRatingSummary() {
		List<RatingAdminResponse> response = ratingService.getAllDocumentRatingSummary();
		APIResponse<RatingAdminResponse> apiResponse = new APIResponse<RatingAdminResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/lesson")
	public APIResponse<RatingAdminResponse> getAllLessonRatingSummary() {
		List<RatingAdminResponse> response = ratingService.getAllLessonRatingSummary();
		APIResponse<RatingAdminResponse> apiResponse = new APIResponse<RatingAdminResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

}
