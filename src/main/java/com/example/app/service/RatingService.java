package com.example.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.app.model.Rating;
import com.example.app.repository.RatingRepository;

@Service
public class RatingService {
	private final RatingRepository ratingRepository;

	public RatingService(RatingRepository ratingRepository) {
		this.ratingRepository = ratingRepository;
	}

	public List<Rating> getByDocument(Long docId) {
		return ratingRepository.findByDocumentId(docId);
	}

	public Optional<Rating> getByDocumentAndUser(Long docId, Long userId) {
		return ratingRepository.findByDocumentIdAndUserId(docId, userId);
	}

	public Rating save(Rating rating) {
		return ratingRepository.save(rating);
	}
}
