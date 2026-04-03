package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.FavoriteRequest;
import com.example.app.dto.response.favorite.FavoriteResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.FavoriteMapper;
import com.example.app.model.Document;
import com.example.app.model.Favorite;
import com.example.app.model.Lesson;
import com.example.app.model.User;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.FavoriteRepository;
import com.example.app.repository.LessonRepository;
import com.example.app.share.GetUserByToken;
import com.example.app.share.Status;
import com.example.app.share.Type;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FavoriteService {

	private final FavoriteRepository favoriteRepository;
	private final DocumentRepository documentRepository;
	private final LessonRepository lessonRepository;
	private final FavoriteMapper favoriteMapper;
	private final GetUserByToken getUserByToken;

	@PreAuthorize("hasAuthority('ADD_FAVORITE')")
	public FavoriteResponse addFavorite(FavoriteRequest dto) {
		Favorite favorite = favoriteMapper.requestToFavorite(dto);

		if (favorite.getType() == Type.DOCUMENT) {
			Document doc = documentRepository.findById(dto.getContentId())
					.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));
			favorite.setDocument(doc);
		} else {
			Lesson lesson = lessonRepository.findById(dto.getContentId())
					.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));
			favorite.setLesson(lesson);
		}

		User user = getUserByToken.get();

		favorite.setCreatedAt(LocalDateTime.now());
		favorite.setUser(user);
		Favorite saved = favoriteRepository.save(favorite);
		FavoriteResponse response = favoriteMapper.favoriteDocumentToResponse(saved);
		return response;
	}

	@PreAuthorize("hasAuthority('GET_DOCUMENT_FAVORITE')")
	public List<FavoriteResponse> getDocumentFavoritesByUser() {
		User user = getUserByToken.get();
		List<Favorite> favorites = favoriteRepository.findByUser_Id(user.getId());
		List<FavoriteResponse> response = new ArrayList<FavoriteResponse>();
		for (Favorite f : favorites) {
			if (f.getDocument() != null) {
				Document doc = documentRepository.findById(f.getDocument().getId())
						.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));
				Status status = Status.PUBLISHED;
				if (doc.isHide() == false && doc.getStatus() == status) {
					response.add(favoriteMapper.favoriteDocumentToResponse(f));
				}

			}
		}
		return response;
	}

	@PreAuthorize("hasAuthority('GET_LESSON_FAVORITE')")
	public List<FavoriteResponse> getLessonFavoritesByUser() {
		User user = getUserByToken.get();
		List<Favorite> favorites = favoriteRepository.findByUser_Id(user.getId());
		List<FavoriteResponse> response = new ArrayList<FavoriteResponse>();
		for (Favorite f : favorites) {
			if (f.getLesson() != null) {
				Lesson lesson = lessonRepository.findById(f.getLesson().getId())
						.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));
				Status status = Status.PUBLISHED;
				if (lesson.isHide() == false && lesson.getStatus() == status) {
					response.add(favoriteMapper.favoriteLessonToResponse(f));
				}
			}
		}
		return response;
	}

	@PreAuthorize("hasAuthority('REMOVE_FAVORITE')")
	public void removeFavorite(Long id) {
		try {
			favoriteRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new AppException("Favorite không tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAuthority('REMOVE_DOCUMENT_FAVORITE')")
	public void removeDocumentFavorite(Long DocumentId) {
		User user = getUserByToken.get();
		try {
			Favorite favorite = favoriteRepository.findByUser_IdAndDocument_Id(user.getId(), DocumentId)
					.orElseThrow(() -> new AppException("favorite không tồn tại", 1001, HttpStatus.BAD_REQUEST));
			favoriteRepository.deleteById(favorite.getId());
		} catch (EmptyResultDataAccessException e) {
			throw new AppException("Favorite không tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAuthority('REMOVE_LESSON_FAVORITE')")
	public void removeLessonFavorite(Long LessonId) {
		User user = getUserByToken.get();
		try {
			Favorite favorite = favoriteRepository.findByUser_IdAndLesson_Id(user.getId(), LessonId)
					.orElseThrow(() -> new AppException("favorite không tồn tại", 1001, HttpStatus.BAD_REQUEST));
			favoriteRepository.deleteById(favorite.getId());
		} catch (EmptyResultDataAccessException e) {
			throw new AppException("Favorite không tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAuthority('CHECK_DOCUMENT_FAVORITE')")
	public boolean checkDocumentFavorite(Long documentId) {
		User user = getUserByToken.get();
		return favoriteRepository.existsByUser_IdAndDocument_Id(user.getId(), documentId);
	}

	@PreAuthorize("hasAuthority('CHECK_LESSON_FAVORITE')")
	public boolean checkLessonFavorite(Long LessonId) {
		User user = getUserByToken.get();
		return favoriteRepository.existsByUser_IdAndLesson_Id(user.getId(), LessonId);
	}
}
