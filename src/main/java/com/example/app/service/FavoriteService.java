package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.FavoriteDocumentRequest;
import com.example.app.dto.request.FavoriteLessonRequest;
import com.example.app.dto.response.FavoriteDocumentResponse;
import com.example.app.dto.response.FavoriteLessonResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.FavoriteMapper;
import com.example.app.model.Document;
import com.example.app.model.Favorite;
import com.example.app.model.Lesson;
import com.example.app.model.User;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.FavoriteRepository;
import com.example.app.repository.LessonRepository;
import com.example.app.repository.UserRepository;
import com.example.app.share.GetUserByToken;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FavoriteService {

	private final FavoriteRepository favoriteRepository;
	private final UserRepository userRepository;
	private final DocumentRepository documentRepository;
	private final LessonRepository lesonRepository;
	private final FavoriteMapper favoriteMapper;
	private final GetUserByToken getUserByToken;

	@PreAuthorize("hasAuthority('ADD_DOCUMENT_FAVORITE')")
	public FavoriteDocumentResponse addDocumentFavorite(FavoriteDocumentRequest dto) {
		Favorite favorite = new Favorite();
		Document doc = documentRepository.findById(dto.getDocumentId())
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new AppException("user không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		if (favoriteRepository.existsByUserAndDocument(user, doc)) {
			throw new AppException("đã có trong kho Favorite", 1001, HttpStatus.BAD_REQUEST);
		}
		favorite.setCreatedAt(LocalDateTime.now());
		favorite.setDocument(doc);
		favorite.setUser(user);
		Favorite saved = favoriteRepository.save(favorite);
		FavoriteDocumentResponse response = favoriteMapper.favoriteDocumentToResponse(saved);
		return response;
	}

	@PreAuthorize("hasAuthority('ADD_LESSON_FAVORITE')")
	public FavoriteDocumentResponse addLessonFavorite(FavoriteLessonRequest dto) {
		Favorite favorite = new Favorite();
		Lesson lesson = lesonRepository.findById(dto.getLessonId())
				.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new AppException("user không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		if (favoriteRepository.existsByUserAndLesson(user, lesson)) {
			throw new AppException("đã có trong kho Favorite", 1001, HttpStatus.BAD_REQUEST);
		}
		favorite.setCreatedAt(LocalDateTime.now());
		favorite.setLesson(lesson);
		favorite.setUser(user);
		Favorite saved = favoriteRepository.save(favorite);
		FavoriteDocumentResponse response = favoriteMapper.favoriteDocumentToResponse(saved);
		return response;
	}

	@PreAuthorize("hasAuthority('GET_DOCUMENT_FAVORITE')")
	public List<FavoriteDocumentResponse> getDocumentFavoritesByUser() {
		User user = getUserByToken.get();
		List<Favorite> favorites = favoriteRepository.findByUserId(user.getId());
		List<FavoriteDocumentResponse> response = new ArrayList<FavoriteDocumentResponse>();
		for (Favorite f : favorites) {
			if (f.getDocument() != null) {
				response.add(favoriteMapper.favoriteDocumentToResponse(f));
			}
		}
		return response;
	}

	@PreAuthorize("hasAuthority('GET_LESSON_FAVORITE')")
	public List<FavoriteLessonResponse> getLessonFavoritesByUser() {
		User user = getUserByToken.get();
		List<Favorite> favorites = favoriteRepository.findByUserId(user.getId());
		List<FavoriteLessonResponse> response = new ArrayList<FavoriteLessonResponse>();
		for (Favorite f : favorites) {
			if (f.getLesson() != null) {
				response.add(favoriteMapper.favoriteLessonToResponse(f));
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
}
