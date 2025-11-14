package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.model.Rating;
import com.example.app.service.RatingService;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {
	private final RatingService ratingService;

	public RatingController(RatingService ratingService) {
		this.ratingService = ratingService;
	}

	@GetMapping("/document/{docId}")
	public List<Rating> getByDocument(@PathVariable Long docId) {
		return ratingService.getByDocument(docId);
	}

	@PostMapping
	public Rating create(@RequestBody Rating rating) {
		return ratingService.save(rating);
	}
}
