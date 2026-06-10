package com.example.app.controller;

import com.example.app.dto.request.FavoriteRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.favorite.FavoriteResponse;
import com.example.app.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@AllArgsConstructor
@Slf4j
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public APIResponse<FavoriteResponse> addFavorite(@RequestBody @Valid FavoriteRequest dto) {
        APIResponse<FavoriteResponse> apiResponse = new APIResponse<FavoriteResponse>();
        apiResponse.setResult(favoriteService.addFavorite(dto));
        return apiResponse;
    }

    @DeleteMapping("/document/{documentId}")
    public APIResponse<Void> removeDocumentFavorite(@PathVariable Long documentId) {
        favoriteService.removeDocumentFavorite(documentId);
        return APIResponse.<Void>builder().build();
    }

    @DeleteMapping("/lesson/{lessonId}")
    public APIResponse<Void> removeLessonFavorite(@PathVariable Long lessonId) {
        favoriteService.removeLessonFavorite(lessonId);
        return APIResponse.<Void>builder().build();
    }

    @GetMapping("/document/user")
    public APIResponse<FavoriteResponse> getDocumentFavoritesByUser() {
        APIResponse<FavoriteResponse> apiResponse = new APIResponse<FavoriteResponse>();
        apiResponse.setResultList(favoriteService.getDocumentFavoritesByUser());
        return apiResponse;
    }

    @GetMapping("/lesson/user")
    public APIResponse<FavoriteResponse> getLessonFavoritesByUser() {
        APIResponse<FavoriteResponse> apiResponse = new APIResponse<FavoriteResponse>();
        apiResponse.setResultList(favoriteService.getLessonFavoritesByUser());
        return apiResponse;
    }

    @GetMapping("/document/user/check/{documentId}")
    public APIResponse<Boolean> checkDocumentFavorite(@PathVariable Long documentId) {
        APIResponse<Boolean> apiResponse = new APIResponse<Boolean>();
        apiResponse.setResult(favoriteService.checkDocumentFavorite(documentId));
        return apiResponse;
    }

    @GetMapping("/lesson/user/check/{lessonId}")
    public APIResponse<Boolean> checkLessonFavorite(@PathVariable Long lessonId) {
        APIResponse<Boolean> apiResponse = new APIResponse<Boolean>();
        apiResponse.setResult(favoriteService.checkLessonFavorite(lessonId));
        return apiResponse;
    }

}
