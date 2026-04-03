package com.example.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.RatingRequest;
import com.example.app.dto.response.rating.RatingAdminResponse;
import com.example.app.dto.response.rating.RatingDetailAdminResponse;
import com.example.app.dto.response.rating.RatingSummaryResponse;
import com.example.app.dto.response.rating.RatingUserResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.RatingMapper;
import com.example.app.model.Document;
import com.example.app.model.Lesson;
import com.example.app.model.Rating;
import com.example.app.model.User;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.LessonRepository;
import com.example.app.repository.RatingRepository;
import com.example.app.share.GetUserByToken;
import com.example.app.share.Type;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RatingService {
	private final RatingRepository ratingRepository;
	private final DocumentRepository documentRepository;
	private final LessonRepository lessonRepository;
	private final RatingMapper ratingMapper;
	private final GetUserByToken getUserByToken;

	@PreAuthorize("hasRole('ADMIN')")
	public RatingDetailAdminResponse getByDocument(Long docId) {
		RatingDetailAdminResponse response = ratingRepository.getRatingDetailByDocument(docId);
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public RatingDetailAdminResponse getByLesson(Long lessonId) {
		RatingDetailAdminResponse response = ratingRepository.getRatingDetailByLesson(lessonId);
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<RatingAdminResponse> getAllDocumentRatingSummary() {
		List<RatingAdminResponse> response = ratingRepository.getAllDocumentRatingSummary();
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<RatingAdminResponse> getAllLessonRatingSummary() {
		List<RatingAdminResponse> response = ratingRepository.getAllLessonRatingSummary();
		return response;
	}

	@PreAuthorize("hasAuthority('GET_MY_DOCUMENT_RATING')")
	public Integer getMyRatingDocument(Long docId) {
		User user = getUserByToken.get();
		Integer response = ratingRepository.getMyRatingDocument(docId, user.getId());
		return response;
	}

	@PreAuthorize("hasAuthority('GET_MY_LESSON_RATING')")
	public Integer getMyRatingLesson(Long lessonId) {
		User user = getUserByToken.get();
		Integer response = ratingRepository.getMyRatingLesson(lessonId, user.getId());
		return response;
	}

	@PreAuthorize("hasAuthority('POST_RATING')")
	public RatingUserResponse saveRating(RatingRequest dto) {
		Rating rating = ratingMapper.ratingRequestToRating(dto);
		if (rating.getType() == Type.DOCUMENT) {
			Document doc = documentRepository.findById(dto.getContentId())
					.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));
			rating.setDocument(doc);
		} else {
			Lesson lesson = lessonRepository.findById(dto.getContentId())
					.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));
			rating.setLesson(lesson);
		}

		rating.setCreatedAt(LocalDateTime.now());

		User user = getUserByToken.get();
		rating.setUser(user);
		Rating saved = ratingRepository.save(rating);
		RatingUserResponse response = ratingMapper.ratingToRatingResponse(saved);
		return response;
	}

	public RatingSummaryResponse getRatingSummaryDocument(Long docId) {
		RatingSummaryResponse response = ratingRepository.getRatingSummaryDocument(docId);
		return response;
	}

	public RatingSummaryResponse getRatingSummaryLesson(Long lessonId) {
		RatingSummaryResponse response = ratingRepository.getRatingSummaryLesson(lessonId);
		return response;
	}
}
