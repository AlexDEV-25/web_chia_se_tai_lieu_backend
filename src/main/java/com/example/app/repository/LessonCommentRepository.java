package com.example.app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.app.model.LessonComment;

@Repository
public interface LessonCommentRepository extends JpaRepository<LessonComment, Long> {
	List<LessonComment> findByLesson_IdAndHideFalse(Long lessonId);

	List<LessonComment> findByLesson_IdAndHideFalseOrderByLevelAscCreatedAtAsc(Long lessonId);

	Optional<LessonComment> findByIdAndUser_IdAndHideFalse(Long id, Long UserId);

	void deleteByLesson_Id(Long lessonId);

	@Query("""
			SELECT c
			FROM LessonComment c
			WHERE c.createdAt >= :fromDate
			AND c.hide = false
			ORDER BY c.createdAt DESC
			""")
	List<LessonComment> findLessonCommentsLast7Days(@Param("fromDate") LocalDateTime fromDate);
}
