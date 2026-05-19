package com.example.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.constant.AppError;
import com.example.app.constant.ContentStatus;
import com.example.app.constant.InteractionType;
import com.example.app.dto.request.RatingRequest;
import com.example.app.dto.response.rating.RatingAdminResponse;
import com.example.app.dto.response.rating.RatingDetailAdminResponse;
import com.example.app.dto.response.rating.RatingSummaryResponse;
import com.example.app.dto.response.rating.RatingUserResponse;
import com.example.app.exception.AppException;
import com.example.app.helper.GetUserByToken;
import com.example.app.mapper.RatingMapper;
import com.example.app.model.Document;
import com.example.app.model.DocumentRating;
import com.example.app.model.Lesson;
import com.example.app.model.LessonRating;
import com.example.app.model.User;
import com.example.app.repository.DocumentRatingRepository;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.LessonRatingRepository;
import com.example.app.repository.LessonRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RatingService {
	private final DocumentRatingRepository documentRatingRepository;
	private final LessonRatingRepository lessonRatingRepository;
	private final DocumentRepository documentRepository;
	private final LessonRepository lessonRepository;
	private final RatingMapper ratingMapper;
	private final GetUserByToken getUserByToken;

	@PreAuthorize("hasRole('ADMIN')")
	public RatingDetailAdminResponse getByDocument(Long docId) {
		return documentRatingRepository.getDocumentRatingDetail(docId);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public RatingDetailAdminResponse getByLesson(Long lessonId) {
		return lessonRatingRepository.getLessonRatingDetail(lessonId);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<RatingAdminResponse> getAllDocumentRatingSummary() {
		return documentRatingRepository.getAllDocumentRatingSummary(ContentStatus.PUBLISHED);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<RatingAdminResponse> getAllLessonRatingSummary() {
		return lessonRatingRepository.getAllLessonRatingSummary(ContentStatus.PUBLISHED);
	}

	@PreAuthorize("hasAuthority('GET_MY_DOCUMENT_RATING')")
	public Integer getMyRatingDocument(Long docId) {
		User user = getUserByToken.get();
		return documentRatingRepository.getMyDocumentRating(docId, user.getId());
	}

	@PreAuthorize("hasAuthority('GET_MY_LESSON_RATING')")
	public Integer getMyRatingLesson(Long lessonId) {
		User user = getUserByToken.get();
		return lessonRatingRepository.getMyLessonRating(lessonId, user.getId());
	}

	@PreAuthorize("hasAuthority('POST_RATING')")
	public RatingUserResponse saveRating(RatingRequest request) {
		User user = getUserByToken.get();
		if (request.getType() == InteractionType.DOCUMENT) {
			return saveDocumentRating(user, request);
		} else if (request.getType() == InteractionType.LESSON) {
			return saveLessonRating(user, request);
		} else {
			throw AppException.builder().appError(AppError.TYPE_NOT_FOUND).build();
		}
	}

	public RatingSummaryResponse getRatingSummaryDocument(Long docId) {
		return documentRatingRepository.getDocumentRatingSummary(docId);
	}

	public RatingSummaryResponse getRatingSummaryLesson(Long lessonId) {
		return lessonRatingRepository.getLessonRatingSummary(lessonId);
	}

	private RatingUserResponse saveDocumentRating(User user, RatingRequest request) {
		DocumentRating rating = DocumentRating.builder().rating(request.getRating()).user(user)
				.createdAt(LocalDateTime.now()).build();
		Document doc = documentRepository.findById(request.getContentId())
				.orElseThrow(() -> AppException.builder().appError(AppError.DOCUMENT_NOT_FOUND).build());
		if (documentRatingRepository.existsByUserAndDocument(user, doc)) {
			throw AppException.builder().appError(AppError.ALREADY_RATED).build();
		}
		rating.setDocument(doc);
		DocumentRating saved = documentRatingRepository.save(rating);
		return ratingMapper.documentRatingToRatingResponse(saved);
	}

	private RatingUserResponse saveLessonRating(User user, RatingRequest request) {
		LessonRating rating = LessonRating.builder().rating(request.getRating()).user(user)
				.createdAt(LocalDateTime.now()).build();
		Lesson lesson = lessonRepository.findById(request.getContentId())
				.orElseThrow(() -> AppException.builder().appError(AppError.LECTURE_NOT_FOUND).build());
		if (lessonRatingRepository.existsByUserAndLesson(user, lesson)) {
			throw AppException.builder().appError(AppError.ALREADY_RATED).build();
		}
		rating.setLesson(lesson);
		LessonRating saved = lessonRatingRepository.save(rating);
		return ratingMapper.lessonRatingToRatingResponse(saved);
	}
}
