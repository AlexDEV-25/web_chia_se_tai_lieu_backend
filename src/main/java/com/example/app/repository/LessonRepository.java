package com.example.app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.app.constant.ContentStatus;
import com.example.app.dto.response.lesson.LessonResponse;
import com.example.app.dto.response.lesson.LessonStatsResponse;
import com.example.app.dto.response.statistic.CategoryCountResponse;
import com.example.app.model.Category;
import com.example.app.model.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

	// lấy danh sách những bài giảng của chính mình
	List<Lesson> findByUser_Id(Long userId);

	// lấy bài giảng không bị ẩn hay pending
	Optional<Lesson> findByIdAndStatusAndHideFalse(Long Id, ContentStatus status);

	// lấy danh sách bài giảng không bị ẩn hay pending
	List<Lesson> findByStatusAndHideFalse(ContentStatus status);

	// lấy bài giảng của chính mình
	Optional<Lesson> findByIdAndUser_Id(Long id, Long userId);

	// lấy số bài giảng của 1 người đã được duyệt và không bị ẩn
	long countByUser_IdAndStatusAndHideFalse(Long userId, ContentStatus status);

	// lấy số bài giảng của chính mình đăng tải lên
	long countByUser_Id(Long userId);

	@Query(value = """
				SELECT DATE(l.created_at) as stat_date, COUNT(l.id)
				FROM lessons l
				WHERE l.status = :status
				AND (l.hide = 0 OR l.hide IS NULL)
				AND l.created_at >= :fromDate
				GROUP BY DATE(l.created_at)
				ORDER BY DATE(l.created_at)
			""", nativeQuery = true)
	List<Object[]> countLessonByDay(@Param("fromDate") LocalDateTime fromDate, @Param("status") ContentStatus status);

	@Query("""
			  	SELECT new com.example.app.dto.response.statistic.CategoryCountResponse(
			    c.id,
			    c.name,
			    COUNT(l)
			)
				FROM Lesson l
				JOIN Category c ON l.category.id = c.id
				WHERE l.status = :status
				AND (l.hide = false OR l.hide IS NULL)
				GROUP BY c.id, c.name
			""")
	List<CategoryCountResponse> countLessonByCategory(@Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.lesson.LessonStatsResponse(
			        COUNT(l),
			        COALESCE(SUM(l.viewsCount), 0)
			    )
			    FROM Lesson l
			    WHERE l.hide = false
			      AND l.status = :status
			""")
	LessonStatsResponse getStats(@Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.lesson.LessonResponse(
			        l.id,
			        l.title,
			        l.description,
			        l.thumbnailUrl,
			        l.user.username,
			        l.viewsCount,
			        CASE WHEN f IS NOT NULL THEN true ELSE false END
			    )
			    FROM Lesson l
			    LEFT JOIN LessonFavorite f
			        ON  f.user.id = :currentUserId
			    WHERE l.hide = false
			      AND l.status = :status
			      AND (:categoryId IS NULL OR l.category.id = :categoryId)
			      AND (
			            :keyword IS NULL
			            OR l.title LIKE CONCAT('%', :keyword, '%')
			            OR l.description LIKE CONCAT('%', :keyword, '%')
			      )
			""")
	List<LessonResponse> searchWhenLogin(@Param("keyword") String keyword, @Param("categoryId") Long categoryId,
			@Param("currentUserId") Long currentUserId, @Param("status") ContentStatus status);

	@Query("""
			  SELECT new com.example.app.dto.response.lesson.LessonResponse(
			        l.id,
			        l.title,
			        l.description,
			        l.thumbnailUrl,
			        l.user.username,
			        l.viewsCount,
			        false
			    )
			    FROM Lesson l
			    WHERE l.hide = false
			      AND l.status = :status
			      AND (:categoryId IS NULL OR l.category.id = :categoryId)
			      AND (
			            :keyword IS NULL
			            OR l.title LIKE CONCAT('%', :keyword, '%')
			            OR l.description LIKE CONCAT('%', :keyword, '%')
			      )
			""")
	List<LessonResponse> searchWithoutLogin(@Param("keyword") String keyword, @Param("categoryId") Long categoryId,
			@Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.lesson.LessonResponse(
			        l.id,
			        l.title,
			        l.description,
			        l.thumbnailUrl,
			        l.user.username,
			        l.viewsCount,
			        CASE WHEN f.id IS NOT NULL THEN true ELSE false END
			    )
			    FROM Lesson l
			    LEFT JOIN LessonFavorite f
			        ON  f.lesson.id = l.id
			        AND f.user.id = :currentUserId
			    WHERE l.user.id = :authorId
			        AND l.id <> :currentLessonId
			        AND l.status = :status
			        AND l.hide = false
			    ORDER BY l.createdAt DESC
			""")
	List<LessonResponse> getByUserWhenLoginAndDifferentCurrentLesson(@Param("authorId") Long authorId,
			@Param("currentUserId") Long currentUserId, @Param("currentLessonId") Long currentLessonId,
			@Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.lesson.LessonResponse(
			        l.id,
			        l.title,
			        l.description,
			        l.thumbnailUrl,
			        l.user.username,
			        l.viewsCount,
			        false
			    )
			    FROM Lesson l
			    WHERE l.user.id = :authorId
			        AND l.id <> :currentLessonId
			        AND l.status = :status
			        AND l.hide = false
			    ORDER BY l.createdAt DESC
			""")
	List<LessonResponse> getByUserWithoutLoginAndDifferentCurrentLesson(@Param("authorId") Long authorId,
			@Param("currentLessonId") Long currentLessonId, @Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.lesson.LessonResponse(
			        l.id,
			        l.title,
			        l.description,
			        l.thumbnailUrl,
			        l.user.username,
			        l.viewsCount,
			        CASE WHEN f.id IS NOT NULL THEN true ELSE false END
			    )
			    FROM Lesson l
			    LEFT JOIN LessonFavorite f
			        ON  f.lesson.id = l.id
			        AND f.user.id = :currentUserId
			    WHERE l.user.id = :authorId
			        AND l.status = :status
			        AND l.hide = false
			    ORDER BY l.createdAt DESC
			""")
	List<LessonResponse> getByUserWhenLogin(@Param("authorId") Long authorId,
			@Param("currentUserId") Long currentUserId, @Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.lesson.LessonResponse(
			        l.id,
			        l.title,
			        l.description,
			        l.thumbnailUrl,
			        l.user.username,
			        l.viewsCount,
			        false
			    )
			    FROM Lesson l
			    WHERE l.user.id = :authorId
			        AND l.status = :status
			        AND l.hide = false
			    ORDER BY l.createdAt DESC
			""")
	List<LessonResponse> getByUserWithoutLogin(@Param("authorId") Long authorId, @Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.lesson.LessonResponse(
			        l.id,
			        l.title,
			        l.description,
			        l.thumbnailUrl,
			        l.user.username,
			        l.viewsCount,
			        CASE WHEN f.id IS NOT NULL THEN true ELSE false END
			    )
			    FROM Lesson l
			    LEFT JOIN LessonFavorite f
			        ON  f.lesson.id = l.id
			        AND f.user.id = :currentUserId
			    WHERE l.category.id = :categoryId
			        AND l.id <> :currentLessonId
			        AND l.status = :status
			        AND l.hide = false
			    ORDER BY l.createdAt DESC
			""")
	List<LessonResponse> getByCategoryWhenLoginAndDifferentCurrentLesson(@Param("categoryId") Long categoryId,
			@Param("currentUserId") Long currentUserId, @Param("currentLessonId") Long currentLessonId,
			@Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.lesson.LessonResponse(
			        l.id,
			        l.title,
			        l.description,
			        l.thumbnailUrl,
			        l.user.username,
			        l.viewsCount,
			        false
			    )
			    FROM Lesson l
			    WHERE l.category.id = :categoryId
			        AND l.id <> :currentLessonId
			        AND l.status = :status
			        AND l.hide = false
			    ORDER BY l.createdAt DESC
			""")
	List<LessonResponse> getByCategoryWithoutLoginAndDifferentCurrentLesson(@Param("categoryId") Long categoryId,
			@Param("currentLessonId") Long currentLessonId, @Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.lesson.LessonResponse(
			        l.id,
			        l.title,
			        l.description,
			        l.thumbnailUrl,
			        l.user.username,
			        l.viewsCount,
			        CASE WHEN f.id IS NOT NULL THEN true ELSE false END
			    )
			    FROM Lesson l
			    LEFT JOIN LessonFavorite f
			        ON  f.lesson.id = l.id
			        AND f.user.id = :currentUserId
			    WHERE l.status = :status
			        AND l.hide = false
			    ORDER BY l.createdAt DESC
			""")
	List<LessonResponse> getAllWhenLogin(@Param("currentUserId") Long currentUserId,
			@Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.lesson.LessonResponse(
			        l.id,
			        l.title,
			        l.description,
			        l.thumbnailUrl,
			        l.user.username,
			        l.viewsCount,
			        false
			    )
			    FROM Lesson l
			    WHERE l.status = :status
			        AND l.hide = false
			    ORDER BY l.createdAt DESC
			""")
	List<LessonResponse> getAllWithoutLogin(@Param("status") ContentStatus status);

	List<Lesson> findByCategoryAndStatusAndHideFalse(Category category, ContentStatus status);

}
