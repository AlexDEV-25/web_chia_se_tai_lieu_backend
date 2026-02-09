package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.app.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByDocument_IdAndHideFalse(Long documentId);

	List<Comment> findByLesson_IdAndHideFalse(Long lessonId);

}
