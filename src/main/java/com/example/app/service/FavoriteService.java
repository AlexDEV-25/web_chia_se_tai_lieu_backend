package com.example.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.constant.AppError;
import com.example.app.constant.ContentStatus;
import com.example.app.constant.InteractionType;
import com.example.app.dto.request.FavoriteRequest;
import com.example.app.dto.response.favorite.FavoriteResponse;
import com.example.app.exception.AppException;
import com.example.app.helper.GetUserByToken;
import com.example.app.mapper.FavoriteMapper;
import com.example.app.model.Document;
import com.example.app.model.DocumentFavorite;
import com.example.app.model.Lesson;
import com.example.app.model.LessonFavorite;
import com.example.app.model.User;
import com.example.app.repository.DocumentFavoriteRepository;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.LessonFavoriteRepository;
import com.example.app.repository.LessonRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FavoriteService {

	private final DocumentFavoriteRepository favoriteDocumentRepository;
	private final LessonFavoriteRepository favoriteLessonRepository;
	private final DocumentRepository documentRepository;
	private final LessonRepository lessonRepository;
	private final FavoriteMapper favoriteMapper;
	private final GetUserByToken getUserByToken;

	@PreAuthorize("hasAuthority('ADD_FAVORITE')")
	public FavoriteResponse addFavorite(FavoriteRequest request) {
		User user = getUserByToken.get();

		if (request.getType() == InteractionType.DOCUMENT) {
			return saveDocumentFavorite(user, request.getContentId());
		} else if (request.getType() == InteractionType.LESSON) {
			return saveLessonFavorite(user, request.getContentId());
		} else {
			throw AppException.builder().appError(AppError.ADD_TO_FAVORITE_FAILED).build();
		}
	}

	@PreAuthorize("hasAuthority('GET_DOCUMENT_FAVORITE')")
	public List<FavoriteResponse> getDocumentFavoritesByUser() {
		User user = getUserByToken.get();
		List<DocumentFavorite> favorites = favoriteDocumentRepository.findByUserIdAndDocumentFit(user.getId(),
				ContentStatus.PUBLISHED);
		List<FavoriteResponse> response = favorites.stream().map(favoriteMapper::documentFavoriteToResponse).toList();
		return response;
	}

	@PreAuthorize("hasAuthority('GET_LESSON_FAVORITE')")
	public List<FavoriteResponse> getLessonFavoritesByUser() {
		User user = getUserByToken.get();
		List<LessonFavorite> favorites = favoriteLessonRepository.findByUserIdAndLessonFit(user.getId(),
				ContentStatus.PUBLISHED);
		List<FavoriteResponse> response = favorites.stream().map(favoriteMapper::lessonFavoriteToResponse).toList();
		return response;
	}

	@PreAuthorize("hasAuthority('REMOVE_DOCUMENT_FAVORITE')")
	public void removeDocumentFavorite(Long documentId) {
		User user = getUserByToken.get();
		DocumentFavorite favorite = favoriteDocumentRepository.findByUser_IdAndDocument_Id(user.getId(), documentId)
				.orElseThrow(() -> AppException.builder().appError(AppError.REMOVE_FROM_FAVORITE_FAILED).build());
		favoriteDocumentRepository.deleteById(favorite.getId());

	}

	@PreAuthorize("hasAuthority('REMOVE_LESSON_FAVORITE')")
	public void removeLessonFavorite(Long LessonId) {
		User user = getUserByToken.get();
		LessonFavorite favorite = favoriteLessonRepository.findByUser_IdAndLesson_Id(user.getId(), LessonId)
				.orElseThrow(() -> AppException.builder().appError(AppError.REMOVE_FROM_FAVORITE_FAILED).build());
		favoriteLessonRepository.deleteById(favorite.getId());

	}

	@PreAuthorize("hasAuthority('CHECK_DOCUMENT_FAVORITE')")
	public boolean checkDocumentFavorite(Long documentId) {
		User user = getUserByToken.get();
		return favoriteDocumentRepository.existsByUser_IdAndDocument_Id(user.getId(), documentId);
	}

	@PreAuthorize("hasAuthority('CHECK_LESSON_FAVORITE')")
	public boolean checkLessonFavorite(Long LessonId) {
		User user = getUserByToken.get();
		return favoriteLessonRepository.existsByUser_IdAndLesson_Id(user.getId(), LessonId);
	}

	private FavoriteResponse saveDocumentFavorite(User user, Long contentId) {
		DocumentFavorite favorite = DocumentFavorite.builder().createdAt(LocalDateTime.now()).user(user).build();
		Document doc = documentRepository.findById(contentId)
				.orElseThrow(() -> AppException.builder().appError(AppError.DOCUMENT_NOT_FOUND).build());
		favorite.setDocument(doc);
		DocumentFavorite saved = favoriteDocumentRepository.save(favorite);
		return favoriteMapper.documentFavoriteToResponse(saved);
	}

	private FavoriteResponse saveLessonFavorite(User user, Long contentId) {
		LessonFavorite favorite = LessonFavorite.builder().createdAt(LocalDateTime.now()).user(user).build();
		Lesson lesson = lessonRepository.findById(contentId)
				.orElseThrow(() -> AppException.builder().appError(AppError.LECTURE_NOT_FOUND).build());
		favorite.setLesson(lesson);
		LessonFavorite saved = favoriteLessonRepository.save(favorite);
		return favoriteMapper.lessonFavoriteToResponse(saved);
	}
}
