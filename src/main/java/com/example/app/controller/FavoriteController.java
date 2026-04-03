package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.FavoriteRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.favorite.FavoriteResponse;
import com.example.app.service.FavoriteService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/favorites")
@AllArgsConstructor
@Slf4j
public class FavoriteController {

	private final FavoriteService favoriteService;

	@PostMapping("/")
	public APIResponse<FavoriteResponse> addFavorite(@RequestBody @Valid FavoriteRequest dto) {
		FavoriteResponse response = favoriteService.addFavorite(dto);
		APIResponse<FavoriteResponse> apiResponse = new APIResponse<FavoriteResponse>();
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

	@DeleteMapping("document/{documentId}")
	public APIResponse<Void> removeDocumentFavorite(@PathVariable Long documentId) {
		favoriteService.removeDocumentFavorite(documentId);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	@DeleteMapping("lesson/{lessonId}")
	public APIResponse<Void> removeLessonFavorite(@PathVariable Long lessonId) {
		favoriteService.removeLessonFavorite(lessonId);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	@GetMapping("/document/user")
	public APIResponse<FavoriteResponse> getDocumentFavoritesByUser() {
		List<FavoriteResponse> response = favoriteService.getDocumentFavoritesByUser();
		APIResponse<FavoriteResponse> apiResponse = new APIResponse<FavoriteResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/lesson/user")
	public APIResponse<FavoriteResponse> getLessonFavoritesByUser() {
		List<FavoriteResponse> response = favoriteService.getLessonFavoritesByUser();
		APIResponse<FavoriteResponse> apiResponse = new APIResponse<FavoriteResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/document/user/check/{documentId}")
	public APIResponse<Boolean> checkDocumentFavorite(@PathVariable Long documentId) {
		Boolean response = favoriteService.checkDocumentFavorite(documentId);
		APIResponse<Boolean> apiResponse = new APIResponse<Boolean>();
		apiResponse.setResult(response);
		apiResponse.setMessage("check all success");
		return apiResponse;
	}

	@GetMapping("/lesson/user/check/{lessonId}")
	public APIResponse<Boolean> checkLessonFavorite(@PathVariable Long lessonId) {
		Boolean response = favoriteService.checkLessonFavorite(lessonId);
		APIResponse<Boolean> apiResponse = new APIResponse<Boolean>();
		apiResponse.setResult(response);
		apiResponse.setMessage("check all success");
		return apiResponse;
	}

}
