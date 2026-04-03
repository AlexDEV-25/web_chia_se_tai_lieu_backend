package com.example.app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.app.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByDocument_IdAndHideFalse(Long documentId);

	List<Comment> findByLesson_IdAndHideFalse(Long lessonId);

	Optional<Comment> findByIdAndUser_IdAndHideFalse(Long id, Long UserId);

	@Query("""
			SELECT c
			FROM Comment c
			WHERE c.createdAt >= :fromDate
			AND c.hide = false
			ORDER BY c.createdAt DESC
			""")
	List<Comment> findCommentsLast7Days(@Param("fromDate") LocalDateTime fromDate);

	List<Comment> findByDocument_IdAndHideFalseOrderByLevelAscCreatedAtAsc(Long documentId);

	List<Comment> findByLesson_IdAndHideFalseOrderByLevelAscCreatedAtAsc(Long lessonId);
}
