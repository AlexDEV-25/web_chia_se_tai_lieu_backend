package com.example.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.CategoryDTO;
import com.example.app.model.Category;
import com.example.app.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@GetMapping("/{id}")
	public CategoryDTO getById(@PathVariable Long id) {
		Category category = categoryService.findById(id);
		return new CategoryDTO(category.getId(), category.getName(), category.getDescription(), category.isHide());
	}

	@GetMapping
	public List<CategoryDTO> getAll() {
		return categoryService.findAll().stream()
				.map(cat -> new CategoryDTO(cat.getId(), cat.getName(), cat.getDescription(), cat.isHide()))
				.collect(Collectors.toList());
	}

	@PostMapping
	public CategoryDTO create(@RequestBody CategoryDTO dto) {
		Category category = new Category();
		category.setName(dto.getName());
		category.setDescription(dto.getDescription());
		category.setHide(dto.isHide());

		Category saved = categoryService.save(category);

		return new CategoryDTO(saved.getId(), saved.getName(), saved.getDescription(), saved.isHide());
	}

	@PutMapping("/{id}")
	public CategoryDTO update(@PathVariable Long id, @RequestBody CategoryDTO dto) {
		Category category = new Category();
		category.setName(dto.getName());
		category.setDescription(dto.getDescription());
		Category saved = categoryService.update(id, category);
		return new CategoryDTO(saved.getId(), saved.getName(), saved.getDescription(), saved.isHide());
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		categoryService.delete(id);
	}
}
