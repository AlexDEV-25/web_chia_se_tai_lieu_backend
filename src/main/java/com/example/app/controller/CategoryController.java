package com.example.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.category.CategoryResponse;
import com.example.app.service.CategoryService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@AllArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;

	@GetMapping
	public APIResponse<CategoryResponse> getAllPublicCategories() {
		APIResponse<CategoryResponse> apiResponse = new APIResponse<CategoryResponse>();
		apiResponse.setResultList(categoryService.getAllPublicCategories());
		return apiResponse;
	}

}
