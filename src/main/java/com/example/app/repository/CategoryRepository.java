package com.example.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	Category findById(long id);

	boolean existsById(Long id);
}