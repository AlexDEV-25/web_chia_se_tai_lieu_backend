package com.example.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.RatingRequest;
import com.example.app.dto.response.RatingResponse;
import com.example.app.mapper.RatingMapper;
import com.example.app.model.Document;
import com.example.app.model.Rating;
import com.example.app.model.User;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.RatingRepository;
import com.example.app.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RatingService {
	private final RatingRepository ratingRepository;
	private final DocumentRepository documentRepository;
	private final UserRepository userRepository;
	private final RatingMapper ratingMapper;

	public List<RatingResponse> getByDocument(Long docId) {
		List<Rating> ratings = ratingRepository.findByDocumentId(docId);
		List<RatingResponse> response = new ArrayList<RatingResponse>();
		for (Rating r : ratings) {
			response.add(ratingMapper.ratingToResponse(r));
		}
		return response;
	}

// chưa biết làm gì với nó
	public RatingResponse getByDocumentAndUser(RatingRequest dto) {
		Rating rating = ratingRepository.findByDocumentIdAndUserId(dto.getDocumentId(), dto.getUserId())
				.orElseThrow(() -> new RuntimeException("not found"));
		RatingResponse response = ratingMapper.ratingToResponse(rating);
		return response;
	}

	@PreAuthorize("hasAuthority('POST_RATING')")
	public RatingResponse save(RatingRequest dto) {
		Rating rating = ratingMapper.requestToRating(dto);
		Document doc = documentRepository.findById(dto.getDocumentId())
				.orElseThrow(() -> new RuntimeException("Document not found"));

		User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
		rating.setDocument(doc);
		rating.setUser(user);
		Rating saved = ratingRepository.save(rating);
		RatingResponse response = ratingMapper.ratingToResponse(saved);
		return response;
	}
}
