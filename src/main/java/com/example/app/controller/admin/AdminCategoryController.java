package com.example.app.controller.admin;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.CategoryRequest;
import com.example.app.dto.request.DisplayRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.category.CategoryResponse;
import com.example.app.service.CategoryService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/categories/admin")
@AllArgsConstructor
public class AdminCategoryController {
	private final CategoryService categoryService;

	@GetMapping("/{id}")
	public APIResponse<CategoryResponse> getById(@PathVariable Long id) {
		APIResponse<CategoryResponse> apiResponse = new APIResponse<CategoryResponse>();
		apiResponse.setResult(categoryService.findById(id));
		return apiResponse;
	}

	@GetMapping
	public APIResponse<CategoryResponse> getAllCategory() {
		APIResponse<CategoryResponse> apiResponse = new APIResponse<CategoryResponse>();
		apiResponse.setResultList(categoryService.getAllCategories());
		return apiResponse;
	}

	@PostMapping
	public APIResponse<CategoryResponse> create(@RequestBody @Valid CategoryRequest dto) {
		APIResponse<CategoryResponse> apiResponse = new APIResponse<CategoryResponse>();
		apiResponse.setResult(categoryService.save(dto));
		return apiResponse;
	}

	@PutMapping("/{id}")
	public APIResponse<CategoryResponse> update(@PathVariable Long id, @RequestBody @Valid CategoryRequest dto) {
		APIResponse<CategoryResponse> apiResponse = new APIResponse<CategoryResponse>();
		apiResponse.setResult(categoryService.update(id, dto));
		return apiResponse;
	}

	@PutMapping("/hide/{id}")
	public APIResponse<CategoryResponse> hide(@PathVariable Long id, @RequestBody @Valid DisplayRequest dto) {
		APIResponse<CategoryResponse> apiResponse = new APIResponse<CategoryResponse>();
		apiResponse.setResult(categoryService.display(id, dto));
		return apiResponse;
	}

	@DeleteMapping("/{id}")
	public APIResponse<Void> delete(@PathVariable Long id) {
		categoryService.delete(id);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		return apiResponse;
	}
}
