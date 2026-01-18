package com.example.app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.app.dto.response.CategoryCountResponse;
import com.example.app.dto.response.DailyCountResponse;
import com.example.app.model.Lesson;

import feign.Param;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
	List<Lesson> findByCategoryId(Long categoryId);

	Lesson findByLessonUrl(String lessonUrl);

	List<Lesson> findByUserId(Long userId);

	Optional<Lesson> findByIdAndUserId(Long id, Long userId);

	boolean existsById(Long id);

	@Query("""
				SELECT new com.example.app.dto.response.DailyCountResponse(
			    CAST(FUNCTION('date', l.createdAt) AS java.time.LocalDate),
			    COUNT(l)
			)
				FROM Lesson l
				WHERE l.status = 'PUBLISHED'
				AND (l.hide = false OR l.hide IS NULL)
				AND l.createdAt >= :fromDate
				GROUP BY FUNCTION('date', l.createdAt)
				ORDER BY FUNCTION('date', l.createdAt)
			""")
	List<DailyCountResponse> countLessonByDay(@Param("fromDate") LocalDateTime fromDate);

	@Query("""
			  	SELECT new com.example.app.dto.response.CategoryCountResponse(
			    c.id,
			    c.name,
			    COUNT(l)
			)
				FROM Lesson l
				JOIN l.category c
				WHERE l.status = 'PUBLISHED'
				AND (l.hide = false OR l.hide IS NULL)
				GROUP BY c.id, c.name
			""")
	List<CategoryCountResponse> countLessonByCategory();
}
