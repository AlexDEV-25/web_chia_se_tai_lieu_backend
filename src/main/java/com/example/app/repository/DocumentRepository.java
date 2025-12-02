package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.app.model.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
	List<Document> findByCategoryId(Long categoryId);

	Document findByFileUrl(String FileUrl);

	List<Document> findByUserId(Long userId);

	boolean existsById(Long id);
}
