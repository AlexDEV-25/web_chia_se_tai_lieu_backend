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
import com.example.app.model.User;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.FavoriteRepository;
import com.example.app.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FavoriteService {

	private final FavoriteRepository favoriteRepository;
	private final UserRepository userRepository;
	private final DocumentRepository documentRepository;
	private final FavoriteMapper favoriteMapper;

	@PreAuthorize("hasRole('USER')")
	public FavoriteResponse addFavorite(FavoriteRequest dto) {
		Favorite favorite = favoriteMapper.requestToFavorite(dto);
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
		FavoriteResponse response = favoriteMapper.favoriteToResponse(saved);
		return response;
	}

	@PreAuthorize("hasRole('USER')")
	public void removeFavorite(Long id) {
		try {
			favoriteRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new AppException("Favorite không tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasRole('USER')")
	public List<FavoriteResponse> getFavoritesByUser(Long userId) {
		List<Favorite> favorites = favoriteRepository.findByUserId(userId);
		List<FavoriteResponse> response = new ArrayList<FavoriteResponse>();
		for (Favorite f : favorites) {
			response.add(favoriteMapper.favoriteToResponse(f));
		}
		return response;
	}
}
