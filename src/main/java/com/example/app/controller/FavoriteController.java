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
import com.example.app.dto.response.FavoriteResponse;
import com.example.app.service.FavoriteService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/favorites")
@AllArgsConstructor
public class FavoriteController {

	private final FavoriteService favoriteService;

	@PostMapping
	public APIResponse<FavoriteResponse> addFavorite(@RequestBody FavoriteRequest dto) {
		FavoriteResponse response = favoriteService.addFavorite(dto);
		APIResponse<FavoriteResponse> apiResponse = new APIResponse<FavoriteResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@DeleteMapping("/{id}")
	public APIResponse<FavoriteResponse> removeFavorite(@PathVariable Long id) {
		favoriteService.removeFavorite(id);
		APIResponse<FavoriteResponse> apiResponse = new APIResponse<FavoriteResponse>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	// Get user's favorites
	@GetMapping("/user/{userId}")
	public APIResponse<FavoriteResponse> getFavoritesByUser(@PathVariable Long userId) {
		List<FavoriteResponse> response = favoriteService.getFavoritesByUser(userId);
		APIResponse<FavoriteResponse> apiResponse = new APIResponse<FavoriteResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

}
