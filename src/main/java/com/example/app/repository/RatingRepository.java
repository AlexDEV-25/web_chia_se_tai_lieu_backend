package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.app.model.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

	List<Rating> findByDocumentId(Long documentId);

	boolean existsById(Long id);
}
