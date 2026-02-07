package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.app.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByDocumentIdAndHideFalse(Long documentId);

	List<Comment> findByLessonIdAndHideFalse(Long lessonId);

}
