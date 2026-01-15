package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.RatingRequest;
import com.example.app.dto.response.RatingResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.RatingMapper;
import com.example.app.model.Document;
import com.example.app.model.Lesson;
import com.example.app.model.Rating;
import com.example.app.model.User;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.LessonRepository;
import com.example.app.repository.RatingRepository;
import com.example.app.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RatingService {
	private final RatingRepository ratingRepository;
	private final DocumentRepository documentRepository;
	private final LessonRepository lessonRepository;
	private final UserRepository userRepository;
	private final RatingMapper ratingMapper;

	public List<RatingResponse> getByDocument(Long docId) {
		List<Rating> ratings = ratingRepository.findByDocumentId(docId);
		List<RatingResponse> response = new ArrayList<RatingResponse>();
		for (Rating r : ratings) {
			response.add(ratingMapper.ratingToRatingDocumentResponse(r));
		}
		return response;
	}

	@PreAuthorize("hasAuthority('POST_DOCUMENT_RATING')")
	public RatingResponse saveRatingDocument(RatingRequest dto) {
		Rating rating = ratingMapper.ratingDocumentRequestToRating(dto);
		Document doc = documentRepository.findById(dto.getContentId())
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new AppException("username không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		rating.setCreatedAt(LocalDateTime.now());
		rating.setDocument(doc);
		rating.setUser(user);
		Rating saved = ratingRepository.save(rating);
		RatingResponse response = ratingMapper.ratingToRatingDocumentResponse(saved);
		return response;
	}

	public List<RatingResponse> getByLesson(Long LessonId) {
		List<Rating> ratings = ratingRepository.findByLessonId(LessonId);
		List<RatingResponse> response = new ArrayList<RatingResponse>();
		for (Rating r : ratings) {
			response.add(ratingMapper.ratingToRatingLessonResponse(r));
		}
		return response;
	}

	@PreAuthorize("hasAuthority('POST_LESSON_RATING')")
	public RatingResponse saveRatingLesson(RatingRequest dto) {
		Rating rating = ratingMapper.ratingLessonRequestToRating(dto);
		Lesson lesson = lessonRepository.findById(dto.getContentId())
				.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new AppException("username không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		rating.setCreatedAt(LocalDateTime.now());
		rating.setLesson(lesson);
		rating.setUser(user);
		Rating saved = ratingRepository.save(rating);
		RatingResponse response = ratingMapper.ratingToRatingLessonResponse(saved);
		return response;
	}
}
