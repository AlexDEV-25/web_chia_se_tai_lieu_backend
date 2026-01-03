package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.RatingDocumentRequest;
import com.example.app.dto.request.RatingLessonRequest;
import com.example.app.dto.response.RatingDocumentResponse;
import com.example.app.dto.response.RatingLessonResponse;
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

	public List<RatingDocumentResponse> getByDocument(Long docId) {
		List<Rating> ratings = ratingRepository.findByDocumentId(docId);
		List<RatingDocumentResponse> response = new ArrayList<RatingDocumentResponse>();
		for (Rating r : ratings) {
			response.add(ratingMapper.ratingToRatingDocumentResponse(r));
		}
		return response;
	}

	@PreAuthorize("hasAuthority('POST_DOCUMENT_RATING')")
	public RatingDocumentResponse save(RatingDocumentRequest dto) {
		Rating rating = ratingMapper.ratingDocumentRequestToRating(dto);
		Document doc = documentRepository.findById(dto.getDocumentId())
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new AppException("username không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		rating.setCreatedAt(LocalDateTime.now());
		rating.setDocument(doc);
		rating.setUser(user);
		Rating saved = ratingRepository.save(rating);
		RatingDocumentResponse response = ratingMapper.ratingToRatingDocumentResponse(saved);
		return response;
	}

	public List<RatingLessonResponse> getByLesson(Long LessonId) {
		List<Rating> ratings = ratingRepository.findByLessonId(LessonId);
		List<RatingLessonResponse> response = new ArrayList<RatingLessonResponse>();
		for (Rating r : ratings) {
			response.add(ratingMapper.ratingToRatingLessonResponse(r));
		}
		return response;
	}

	@PreAuthorize("hasAuthority('POST_LESSON_RATING')")
	public RatingLessonResponse save(RatingLessonRequest dto) {
		Rating rating = ratingMapper.ratingLessonRequestToRating(dto);
		Lesson lesson = lessonRepository.findById(dto.getLessonId())
				.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new AppException("username không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		rating.setCreatedAt(LocalDateTime.now());
		rating.setLesson(lesson);
		rating.setUser(user);
		Rating saved = ratingRepository.save(rating);
		RatingLessonResponse response = ratingMapper.ratingToRatingLessonResponse(saved);
		return response;
	}
}
