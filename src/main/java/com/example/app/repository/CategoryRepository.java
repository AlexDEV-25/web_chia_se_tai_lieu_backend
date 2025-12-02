package com.example.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.app.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	Category findById(long id);

	boolean existsById(Long id);
}