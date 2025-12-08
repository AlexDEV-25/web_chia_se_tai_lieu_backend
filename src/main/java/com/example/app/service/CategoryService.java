package com.example.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.CategoryRequest;
import com.example.app.dto.request.HideRequest;
import com.example.app.dto.response.CategoryResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.CategoryMapper;
import com.example.app.model.Category;
import com.example.app.repository.CategoryRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoryService {
	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;

	public List<CategoryResponse> getAllCategories() {
		List<Category> categories = categoryRepository.findAll();
		List<CategoryResponse> responses = new ArrayList<CategoryResponse>();
		for (Category c : categories) {
			responses.add(categoryMapper.categoryToResponse(c));
		}
		return responses;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CategoryResponse findById(Long id) {
		Category find = categoryRepository.findById(id)
				.orElseThrow(() -> new AppException("không tìm thấy danh mục", 1001, HttpStatus.BAD_REQUEST));
		return categoryMapper.categoryToResponse(find);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CategoryResponse save(CategoryRequest request) {
		Category category = categoryMapper.requestToCategory(request);
		Category saved = categoryRepository.save(category);
		CategoryResponse response = categoryMapper.categoryToResponse(saved);
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CategoryResponse update(Long id, CategoryRequest request) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new AppException("không tìm thấy danh mục", 1001, HttpStatus.BAD_REQUEST));
		categoryMapper.updateCategory(category, request);
		Category saved = categoryRepository.save(category);
		return categoryMapper.categoryToResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CategoryResponse hide(Long id, HideRequest request) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new AppException("không tìm thấy danh mục", 1001, HttpStatus.BAD_REQUEST));
		category.setHide(request.isHide());
		Category saved = categoryRepository.save(category);
		return categoryMapper.categoryToResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public void delete(Long id) {
		try {
			categoryRepository.deleteById(id);
		} catch (AppException e) {
			throw new AppException("không tìm thấy danh mục", 1001, HttpStatus.BAD_REQUEST);
		}
	}
}
