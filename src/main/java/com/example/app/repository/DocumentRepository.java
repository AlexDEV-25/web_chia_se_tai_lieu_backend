package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.model.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
	List<Document> findByCategoryId(Long categoryId);

	List<Document> findByUserId(Long userId);
}
