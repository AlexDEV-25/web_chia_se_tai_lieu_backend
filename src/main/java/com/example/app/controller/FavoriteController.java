package com.example.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.model.Favorite;
import com.example.app.service.FavoriteService;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

	private final FavoriteService favoriteService;

	public FavoriteController(FavoriteService favoriteService) {
		this.favoriteService = favoriteService;
	}

	// Add to favorites
	@PostMapping
	public ResponseEntity<Favorite> addFavorite(@RequestParam Long userId, @RequestParam Long documentId) {
		return ResponseEntity.ok(favoriteService.addFavorite(userId, documentId));
	}

	// Remove favorite
	@DeleteMapping("/{favoriteId}")
	public ResponseEntity<String> removeFavorite(@PathVariable Long favoriteId) {
		favoriteService.removeFavorite(favoriteId);
		return ResponseEntity.ok("Favorite removed.");
	}

	// Get user's favorites
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Favorite>> getUserFavorites(@PathVariable Long userId) {
		return ResponseEntity.ok(favoriteService.getFavoritesByUser(userId));
	}

	// Get document's favorites
	@GetMapping("/document/{documentId}")
	public ResponseEntity<List<Favorite>> getDocumentFavorites(@PathVariable Long documentId) {
		return ResponseEntity.ok(favoriteService.getFavoritesByDocument(documentId));
	}
}
