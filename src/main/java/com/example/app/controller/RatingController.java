package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.RatingRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.RatingResponse;
import com.example.app.dto.response.RatingSummaryResponse;
import com.example.app.service.RatingService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/ratings")
@AllArgsConstructor
public class RatingController {
	private final RatingService ratingService;

	@GetMapping("admin/document/{docId}")
	public APIResponse<RatingResponse> getByDocument(@PathVariable Long docId) {
		List<RatingResponse> response = ratingService.getByDocument(docId);
		APIResponse<RatingResponse> apiResponse = new APIResponse<RatingResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("admin/lesson/{lessonId}")
	public APIResponse<RatingResponse> getByLesson(@PathVariable Long lessonId) {
		List<RatingResponse> response = ratingService.getByLesson(lessonId);
		APIResponse<RatingResponse> apiResponse = new APIResponse<RatingResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/document-summary/{documentId}")
	public APIResponse<RatingSummaryResponse> getRatingSummaryDocument(@PathVariable Long documentId) {
		RatingSummaryResponse response = ratingService.getRatingSummaryDocument(documentId);
		APIResponse<RatingSummaryResponse> apiResponse = new APIResponse<RatingSummaryResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/lesson-summary/{lessonId}")
	public APIResponse<RatingSummaryResponse> getRatingSummaryLesson(@PathVariable Long lessonId) {
		RatingSummaryResponse response = ratingService.getRatingSummaryLesson(lessonId);
		APIResponse<RatingSummaryResponse> apiResponse = new APIResponse<RatingSummaryResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/document/my-rating/{documentId}")
	public APIResponse<Integer> getMyRatingDocument(@PathVariable Long documentId) {
		Integer response = ratingService.getMyRatingDocument(documentId);
		APIResponse<Integer> apiResponse = new APIResponse<Integer>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get success");
		return apiResponse;
	}

	@GetMapping("/lesson/my-rating/{lessonId}")
	public APIResponse<Integer> getMyRatingLesson(@PathVariable Long lessonId) {
		Integer response = ratingService.getMyRatingLesson(lessonId);
		APIResponse<Integer> apiResponse = new APIResponse<Integer>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get success");
		return apiResponse;
	}

	@PostMapping("/document")
	public APIResponse<RatingResponse> createRatingDocument(@RequestBody @Valid RatingRequest dto) {
		RatingResponse response = ratingService.saveRatingDocument(dto);
		APIResponse<RatingResponse> apiResponse = new APIResponse<RatingResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@PostMapping("/lesson")
	public APIResponse<RatingResponse> createRatingLesson(@RequestBody @Valid RatingRequest dto) {
		RatingResponse response = ratingService.saveRatingLesson(dto);
		APIResponse<RatingResponse> apiResponse = new APIResponse<RatingResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}
}
