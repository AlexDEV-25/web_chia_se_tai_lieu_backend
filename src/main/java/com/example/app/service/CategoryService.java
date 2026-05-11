package com.example.app.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.constant.HideType;
import com.example.app.dto.request.CategoryRequest;
import com.example.app.dto.request.DisplayRequest;
import com.example.app.dto.response.category.CategoryResponse;
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

	@PreAuthorize("hasRole('ADMIN')")
	public List<CategoryResponse> getAllCategories() {
		List<Category> categories = categoryRepository.findAll();
		List<CategoryResponse> responses = categories.stream().map(categoryMapper::entityToResponse).toList();
		return responses;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CategoryResponse findById(Long id) {
		Category find = categoryRepository.findById(id)
				.orElseThrow(() -> new AppException("không tìm thấy danh mục", 1001, HttpStatus.BAD_REQUEST));
		return categoryMapper.entityToResponse(find);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CategoryResponse save(CategoryRequest request) {
		Category category = Category.builder().name(request.getName()).description(request.getDescription()).hide(false)
				.build();
		Category saved = categoryRepository.save(category);
		return categoryMapper.entityToResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CategoryResponse update(Long id, CategoryRequest request) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new AppException("không tìm thấy danh mục", 1001, HttpStatus.BAD_REQUEST));
		categoryMapper.updateCategory(category, request);
		Category saved = categoryRepository.save(category);
		return categoryMapper.entityToResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CategoryResponse display(Long id, DisplayRequest request) {
		if (request.getType() == HideType.CATEGORY) {
			Category category = categoryRepository.findById(id)
					.orElseThrow(() -> new AppException("không tìm thấy danh mục", 1001, HttpStatus.BAD_REQUEST));
			category.setHide(request.isHide());
			Category saved = categoryRepository.save(category);
			return categoryMapper.entityToResponse(saved);
		} else {
			throw new AppException("không đúng type", 1001, HttpStatus.BAD_REQUEST);
		}

	}

	@PreAuthorize("hasRole('ADMIN')")
	public void delete(Long id) {
		try {
			categoryRepository.deleteById(id);
		} catch (AppException e) {
			throw new AppException("không tìm thấy danh mục", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	public List<CategoryResponse> getAllPublicCategories() {
		List<Category> categories = categoryRepository.findByHideFalse();
		List<CategoryResponse> responses = categories.stream().map(categoryMapper::entityToResponse).toList();
		return responses;
	}

}
