package com.example.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.app.model.Document;
import com.example.app.model.Favorite;
import com.example.app.model.User;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.FavoriteRepository;
import com.example.app.repository.UserRepository;

@Service
public class FavoriteService {

	private final FavoriteRepository favoriteRepository;
	private final UserRepository userRepository;
	private final DocumentRepository documentRepository;

	public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository,
			DocumentRepository documentRepository) {
		this.favoriteRepository = favoriteRepository;
		this.userRepository = userRepository;
		this.documentRepository = documentRepository;
	}

	public Favorite addFavorite(Long userId, Long documentId) {
		User user = userRepository.findById(userId).orElseThrow();
		Document document = documentRepository.findById(documentId).orElseThrow();

		// Avoid duplicate favorite
		if (favoriteRepository.existsByUserAndDocument(user, document)) {
			throw new RuntimeException("Already favorited this document.");
		}

		Favorite favorite = new Favorite();
		favorite.setUser(user);
		favorite.setDocument(document);
		return favoriteRepository.save(favorite);
	}

	public void removeFavorite(Long favoriteId) {
		favoriteRepository.deleteById(favoriteId);
	}

	public List<Favorite> getFavoritesByUser(Long userId) {
		User user = userRepository.findById(userId).orElseThrow();
		return favoriteRepository.findByUser(user);
	}

	public List<Favorite> getFavoritesByDocument(Long documentId) {
		Document document = documentRepository.findById(documentId).orElseThrow();
		return favoriteRepository.findByDocument(document);
	}
}
