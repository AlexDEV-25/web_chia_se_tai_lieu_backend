package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.FavoriteRequest;
import com.example.app.dto.response.FavoriteResponse;
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
import com.example.app.share.Status;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FavoriteService {

	private final FavoriteRepository favoriteRepository;
	private final UserRepository userRepository;
	private final DocumentRepository documentRepository;
	private final LessonRepository lessonRepository;
	private final FavoriteMapper favoriteMapper;
	private final GetUserByToken getUserByToken;

	@PreAuthorize("hasAuthority('ADD_DOCUMENT_FAVORITE')")
	public FavoriteResponse addDocumentFavorite(FavoriteRequest dto) {
		Favorite favorite = new Favorite();
		Document doc = documentRepository.findById(dto.getContentId())
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new AppException("user không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		if (favoriteRepository.existsByUserAndDocument(user, doc)) {
			throw new AppException("đã có trong kho Favorite", 1001, HttpStatus.BAD_REQUEST);
		}
		favorite.setCreatedAt(LocalDateTime.now());
		favorite.setDocument(doc);
		favorite.setUser(user);
		favorite.setType(dto.getType());
		Favorite saved = favoriteRepository.save(favorite);
		FavoriteResponse response = favoriteMapper.favoriteDocumentToResponse(saved);
		return response;
	}

	@PreAuthorize("hasAuthority('ADD_LESSON_FAVORITE')")
	public FavoriteResponse addLessonFavorite(FavoriteRequest dto) {
		Favorite favorite = new Favorite();
		Lesson lesson = lessonRepository.findById(dto.getContentId())
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
		FavoriteResponse response = favoriteMapper.favoriteDocumentToResponse(saved);
		return response;
	}

	@PreAuthorize("hasAuthority('GET_DOCUMENT_FAVORITE')")
	public List<FavoriteResponse> getDocumentFavoritesByUser() {
		User user = getUserByToken.get();
		List<Favorite> favorites = favoriteRepository.findByUserId(user.getId());
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
		List<Favorite> favorites = favoriteRepository.findByUserId(user.getId());
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
}
