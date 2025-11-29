package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByDocumentId(Long documentId);

	boolean existsById(Long id);
}
