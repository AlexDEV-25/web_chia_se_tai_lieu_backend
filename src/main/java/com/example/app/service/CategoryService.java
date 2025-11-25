package com.example.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.app.model.Category;
import com.example.app.repository.CategoryRepository;

@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	public Category findById(Long id) {
		return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy category"));
	}

	public Category save(Category category) {
		return categoryRepository.save(category);
	}

	public void delete(Long id) {
		categoryRepository.deleteById(id);
	}

	public Category update(Long id, Category category) {
		Category data = this.findById(id);
		data.setName(category.getName());
		data.setDescription(category.getDescription());
		return categoryRepository.save(data);
	}
}
