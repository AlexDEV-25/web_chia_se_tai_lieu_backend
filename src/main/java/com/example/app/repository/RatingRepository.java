package com.example.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.model.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
	Optional<Rating> findByDocumentIdAndUserId(Long documentId, Long userId);

	List<Rating> findByDocumentId(Long documentId);

	boolean existsById(Long id);
}
