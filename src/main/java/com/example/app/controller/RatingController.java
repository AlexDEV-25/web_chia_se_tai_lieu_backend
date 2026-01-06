package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.RatingDocumentRequest;
import com.example.app.dto.request.RatingLessonRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.RatingDocumentResponse;
import com.example.app.dto.response.RatingLessonResponse;
import com.example.app.service.RatingService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/ratings")
@AllArgsConstructor
public class RatingController {
	private final RatingService ratingService;

	@GetMapping("/document/{docId}")
	public APIResponse<RatingDocumentResponse> getByDocument(@PathVariable Long docId) {
		List<RatingDocumentResponse> response = ratingService.getByDocument(docId);
		APIResponse<RatingDocumentResponse> apiResponse = new APIResponse<RatingDocumentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PostMapping("/document")
	public APIResponse<RatingDocumentResponse> createRatingDocument(@RequestBody @Valid RatingDocumentRequest dto) {
		RatingDocumentResponse response = ratingService.save(dto);
		APIResponse<RatingDocumentResponse> apiResponse = new APIResponse<RatingDocumentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@GetMapping("/lesson/{lessonId}")
	public APIResponse<RatingLessonResponse> getByLesson(@PathVariable Long lessonId) {
		List<RatingLessonResponse> response = ratingService.getByLesson(lessonId);
		APIResponse<RatingLessonResponse> apiResponse = new APIResponse<RatingLessonResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PostMapping("/lesson")
	public APIResponse<RatingLessonResponse> createRatingLesson(@RequestBody @Valid RatingLessonRequest dto) {
		RatingLessonResponse response = ratingService.save(dto);
		APIResponse<RatingLessonResponse> apiResponse = new APIResponse<RatingLessonResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}
}
