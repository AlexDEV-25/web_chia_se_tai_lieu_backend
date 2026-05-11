package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.app.constant.ContentStatus;
import com.example.app.dto.response.rating.RatingAdminResponse;
import com.example.app.dto.response.rating.RatingDetailAdminResponse;
import com.example.app.dto.response.rating.RatingSummaryResponse;
import com.example.app.model.Lesson;
import com.example.app.model.LessonRating;
import com.example.app.model.User;

@Repository
public interface LessonRatingRepository extends JpaRepository<LessonRating, Long> {
	List<LessonRating> findByLesson_Id(Long lessonId);

	boolean existsByUserAndLesson(User user, Lesson lesson);

	void deleteByLesson_Id(Long lessonId);

	@Query("""
			    SELECT new com.example.app.dto.response.rating.RatingSummaryResponse(
			        AVG(r.rating),
			        COUNT(r.id)
			    )
			    FROM LessonRating r
			    WHERE r.lesson.id = :lessonId
			""")
	RatingSummaryResponse getLessonRatingSummary(@Param("lessonId") Long lessonId);

	@Query("""
			    SELECT COALESCE(r.rating, 0)
			    FROM LessonRating r
			    WHERE r.lesson.id = :lessonId
			    AND r.user.id = :userId
			""")
	Integer getMyLessonRating(@Param("lessonId") Long lessonId, @Param("userId") Long userId);

	@Query("""
			    SELECT new com.example.app.dto.response.rating.RatingAdminResponse(
			        l.id,
			        l.title,
			        COALESCE(AVG(r.rating), 0),
			        COUNT(r)
			    )
			    FROM LessonRating r
			    JOIN r.lesson l
			    WHERE l.status = :status
			    GROUP BY l.id, l.title
			    ORDER BY
			        COALESCE(AVG(r.rating), 0) DESC,
			        COUNT(r) DESC
			""")
	List<RatingAdminResponse> getAllLessonRatingSummary(@Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.rating.RatingDetailAdminResponse(
			        l.id,
			        l.title,
			        SUM(CASE WHEN r.rating = 1 THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.rating = 2 THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.rating = 3 THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.rating = 4 THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.rating = 5 THEN 1 ELSE 0 END)
			    )
			    FROM LessonRating r
			    JOIN r.lesson l
			    WHERE l.id = :lessonId
			    GROUP BY l.id, l.title
			""")
	RatingDetailAdminResponse getLessonRatingDetail(@Param("lessonId") Long lessonId);
}
