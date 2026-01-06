package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.FavoriteDocumentRequest;
import com.example.app.dto.request.FavoriteLessonRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.FavoriteDocumentResponse;
import com.example.app.dto.response.FavoriteLessonResponse;
import com.example.app.service.FavoriteService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/favorites")
@AllArgsConstructor
public class FavoriteController {

	private final FavoriteService favoriteService;

	@PostMapping("/document")
	public APIResponse<FavoriteDocumentResponse> addDocumentFavorite(@RequestBody @Valid FavoriteDocumentRequest dto) {
		FavoriteDocumentResponse response = favoriteService.addDocumentFavorite(dto);
		APIResponse<FavoriteDocumentResponse> apiResponse = new APIResponse<FavoriteDocumentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@PostMapping("/lesson")
	public APIResponse<FavoriteDocumentResponse> addLessonFavorite(@RequestBody @Valid FavoriteLessonRequest dto) {
		FavoriteDocumentResponse response = favoriteService.addLessonFavorite(dto);
		APIResponse<FavoriteDocumentResponse> apiResponse = new APIResponse<FavoriteDocumentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@DeleteMapping("/{id}")
	public APIResponse<Void> removeFavorite(@PathVariable Long id) {
		favoriteService.removeFavorite(id);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	@GetMapping("/document/user")
	public APIResponse<FavoriteDocumentResponse> getDocumentFavoritesByUser() {
		List<FavoriteDocumentResponse> response = favoriteService.getDocumentFavoritesByUser();
		APIResponse<FavoriteDocumentResponse> apiResponse = new APIResponse<FavoriteDocumentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/lesson/user")
	public APIResponse<FavoriteLessonResponse> getLessonFavoritesByUser() {
		List<FavoriteLessonResponse> response = favoriteService.getLessonFavoritesByUser();
		APIResponse<FavoriteLessonResponse> apiResponse = new APIResponse<FavoriteLessonResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

}
