package com.example.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.RatingRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.rating.RatingSummaryResponse;
import com.example.app.dto.response.rating.RatingUserResponse;
import com.example.app.service.RatingService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/ratings")
@AllArgsConstructor
public class RatingController {
	private final RatingService ratingService;

	@GetMapping("/document-summary/{documentId}")
	public APIResponse<RatingSummaryResponse> getRatingSummaryDocument(@PathVariable Long documentId) {
		APIResponse<RatingSummaryResponse> apiResponse = new APIResponse<RatingSummaryResponse>();
		apiResponse.setResult(ratingService.getRatingSummaryDocument(documentId));
		return apiResponse;
	}

	@GetMapping("/lesson-summary/{lessonId}")
	public APIResponse<RatingSummaryResponse> getRatingSummaryLesson(@PathVariable Long lessonId) {
		APIResponse<RatingSummaryResponse> apiResponse = new APIResponse<RatingSummaryResponse>();
		apiResponse.setResult(ratingService.getRatingSummaryLesson(lessonId));
		return apiResponse;
	}

	@GetMapping("/document/my-rating/{documentId}")
	public APIResponse<Integer> getMyRatingDocument(@PathVariable Long documentId) {
		APIResponse<Integer> apiResponse = new APIResponse<Integer>();
		apiResponse.setResult(ratingService.getMyRatingDocument(documentId));
		return apiResponse;
	}

	@GetMapping("/lesson/my-rating/{lessonId}")
	public APIResponse<Integer> getMyRatingLesson(@PathVariable Long lessonId) {
		APIResponse<Integer> apiResponse = new APIResponse<Integer>();
		apiResponse.setResult(ratingService.getMyRatingLesson(lessonId));
		return apiResponse;
	}

	@PostMapping
	public APIResponse<RatingUserResponse> createRating(@RequestBody @Valid RatingRequest dto) {
		APIResponse<RatingUserResponse> apiResponse = new APIResponse<RatingUserResponse>();
		apiResponse.setResult(ratingService.saveRating(dto));
		return apiResponse;
	}

}
