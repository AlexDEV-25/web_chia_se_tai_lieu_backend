package com.example.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.CategoryRequest;
import com.example.app.dto.request.HideRequest;
import com.example.app.dto.response.CategoryResponse;
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

	public CategoryResponse findById(Long id) {
		Category find = categoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy category"));
		return categoryMapper.categoryToResponse(find);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CategoryResponse save(CategoryRequest dto) {
		Category category = categoryMapper.requestToCategory(dto);
		Category saved = categoryRepository.save(category);
		CategoryResponse response = categoryMapper.categoryToResponse(saved);
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CategoryResponse update(Long id, CategoryRequest dto) {
		Category entity = categoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy category"));
		categoryMapper.updateCategory(entity, dto);
		Category saved = categoryRepository.save(entity);
		return categoryMapper.categoryToResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CategoryResponse hide(Long id, HideRequest dto) {
		Category entity = categoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy category"));
		categoryMapper.hideCategory(entity, dto);
		Category saved = categoryRepository.save(entity);
		return categoryMapper.categoryToResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public void delete(Long id) {
		try {
			categoryRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new RuntimeException("Category not found");
		}
	}
}
