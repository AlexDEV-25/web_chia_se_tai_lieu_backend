package com.example.app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.app.dto.response.CategoryCountResponse;
import com.example.app.dto.response.DailyCountResponse;
import com.example.app.dto.response.LessonStatsResponse;
import com.example.app.model.Lesson;
import com.example.app.share.Status;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

	// lấy danh sách những bài giảng của chính mình
	List<Lesson> findByUserId(Long userId);

	// lấy danh sách những bài giảng cùng danh mục nhưng khác với bài giảng đang
	// chọn không bị ẩn hay pending
	List<Lesson> findByIdNotAndCategoryIdAndStatusAndHideFalse(Long lessonId, Long categoryId, Status status);

	// lấy danh sách những bài giảng cùng tác giả nhưng khác với bài giảng đang chọn
	// không bị ẩn hay pending
	List<Lesson> findByIdNotAndUserIdAndStatusAndHideFalse(Long lessonId, Long userId, Status status);

	// lấy bài giảng không bị ẩn hay pending
	Optional<Lesson> findByIdAndStatusAndHideFalse(Long Id, Status status);

	// lấy danh sách bài giảng không bị ẩn hay pending
	List<Lesson> findByStatusAndHideFalse(Status status);

	// lấy bài giảng của chính mình
	Optional<Lesson> findByIdAndUserId(Long id, Long userId);

	// lấy bài giảng không bị ẩn hay pending
	List<Lesson> findByIdNotAndStatusAndHideFalse(Long lessonId, Status status);

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

	@Query("""
			    SELECT new com.example.app.dto.response.LessonStatsResponse(
			        COUNT(l),
			        COALESCE(SUM(l.viewsCount), 0)
			    )
			    FROM Lesson l
			    WHERE l.hide = false
			      AND l.status = 'PUBLISHED'
			""")
	LessonStatsResponse getStats();

	@Query("""
			    SELECT l
			    FROM Lesson l
			    WHERE l.hide = false
			      AND l.status = 'PUBLISHED'
			      AND (:categoryId IS NULL OR l.category.id = :categoryId)
			      AND (
			            :keyword IS NULL
			            OR l.title LIKE CONCAT('%', :keyword, '%')
			            OR l.description LIKE CONCAT('%', :keyword, '%')
			      )
			""")
	List<Lesson> search(@Param("keyword") String keyword, @Param("categoryId") Long categoryId);
}
