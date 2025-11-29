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
import com.example.app.service.RatingService;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
@RequestMapping("/api/ratings")
@Data
@AllArgsConstructor
public class RatingController {
	private final RatingService ratingService;

	@GetMapping("/document/{docId}")
	public APIResponse<RatingResponse> getByDocument(@PathVariable Long docId) {
		List<RatingResponse> response = ratingService.getByDocument(docId);
		APIResponse<RatingResponse> apiResponse = new APIResponse<RatingResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PostMapping
	public APIResponse<RatingResponse> create(@RequestBody RatingRequest dto) {
		RatingResponse response = ratingService.save(dto);
		APIResponse<RatingResponse> apiResponse = new APIResponse<RatingResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}
}
