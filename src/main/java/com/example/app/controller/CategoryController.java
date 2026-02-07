package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.CategoryRequest;
import com.example.app.dto.request.HideRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.CategoryResponse;
import com.example.app.service.CategoryService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@AllArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;

	@GetMapping
	public APIResponse<CategoryResponse> getAllPublicCategories() {
		List<CategoryResponse> response = categoryService.getAllPublicCategories();
		APIResponse<CategoryResponse> apiResponse = new APIResponse<CategoryResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/admin/{id}")
	public APIResponse<CategoryResponse> getById(@PathVariable Long id) {
		CategoryResponse response = categoryService.findById(id);
		APIResponse<CategoryResponse> apiResponse = new APIResponse<CategoryResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping("/admin")
	public APIResponse<CategoryResponse> getAllCategory() {
		List<CategoryResponse> response = categoryService.getAllCategories();
		APIResponse<CategoryResponse> apiResponse = new APIResponse<CategoryResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PostMapping("/admin")
	public APIResponse<CategoryResponse> create(@RequestBody @Valid CategoryRequest dto) {
		CategoryResponse response = categoryService.save(dto);
		APIResponse<CategoryResponse> apiResponse = new APIResponse<CategoryResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@PutMapping("/admin/{id}")
	public APIResponse<CategoryResponse> update(@PathVariable Long id, @RequestBody @Valid CategoryRequest dto) {
		CategoryResponse response = categoryService.update(id, dto);
		APIResponse<CategoryResponse> apiResponse = new APIResponse<CategoryResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("update success");
		return apiResponse;
	}

	@PutMapping("/admin/hide/{id}")
	public APIResponse<CategoryResponse> hide(@PathVariable Long id, @RequestBody @Valid HideRequest dto) {
		CategoryResponse response = categoryService.hide(id, dto);
		APIResponse<CategoryResponse> apiResponse = new APIResponse<CategoryResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("hide success");
		return apiResponse;
	}

	@DeleteMapping("/admin/{id}")
	public APIResponse<CategoryResponse> delete(@PathVariable Long id) {
		categoryService.delete(id);
		APIResponse<CategoryResponse> apiResponse = new APIResponse<CategoryResponse>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}
}
