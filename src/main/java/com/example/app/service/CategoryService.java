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

	public Category save(Category category) {
		return categoryRepository.save(category);
	}

	public void delete(Long id) {
		categoryRepository.deleteById(id);
	}
}
