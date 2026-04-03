package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.app.dto.response.rating.RatingAdminResponse;
import com.example.app.dto.response.rating.RatingDetailAdminResponse;
import com.example.app.dto.response.rating.RatingSummaryResponse;
import com.example.app.model.Document;
import com.example.app.model.Lesson;
import com.example.app.model.Rating;
import com.example.app.model.User;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

	List<Rating> findByDocument_Id(Long documentId);

	List<Rating> findByLesson_Id(Long lessonId);

	boolean existsByUserAndDocument(User user, Document document);

	boolean existsByUserAndLesson(User user, Lesson lesson);

	@Query("""
			    SELECT new com.example.app.dto.response.rating.RatingSummaryResponse(
			        AVG(r.rating),
			        COUNT(r.id)
			    )
			    FROM Rating r
			    WHERE r.document.id = :documentId
			""")
	RatingSummaryResponse getRatingSummaryDocument(@Param("documentId") Long documentId);

	@Query("""
			    SELECT new com.example.app.dto.response.rating.RatingSummaryResponse(
			        AVG(r.rating),
			        COUNT(r.id)
			    )
			    FROM Rating r
			    WHERE r.lesson.id = :lessonId
			""")
	RatingSummaryResponse getRatingSummaryLesson(@Param("lessonId") Long lessonId);

	@Query("""
			    SELECT r.rating
			    FROM Rating r
			    WHERE r.document.id = :documentId
			    AND r.user.id = :userId
			""")
	Integer getMyRatingDocument(@Param("documentId") Long documentId, @Param("userId") Long userId);

	@Query("""
			    SELECT r.rating
			    FROM Rating r
			    WHERE r.lesson.id = :lessonId
			    AND r.user.id = :userId
			""")
	Integer getMyRatingLesson(@Param("lessonId") Long lessonId, @Param("userId") Long userId);

	@Query("""
			    SELECT new com.example.app.dto.response.rating.RatingAdminResponse(
			        d.id,
			        d.title,
			        COALESCE(AVG(r.rating), 0),
			        COUNT(r),
			        com.example.app.share.Type.DOCUMENT
			    )
			    FROM Rating r
			    JOIN r.document d
			    WHERE r.type = com.example.app.share.Type.DOCUMENT
			      AND d.status = com.example.app.share.Status.PUBLISHED
			    GROUP BY d.id, d.title
			    ORDER BY
			        COALESCE(AVG(r.rating), 0) DESC,
			        COUNT(r) DESC
			""")
	List<RatingAdminResponse> getAllDocumentRatingSummary();

	@Query("""
			    SELECT new com.example.app.dto.response.rating.RatingAdminResponse(
			        l.id,
			        l.title,
			        COALESCE(AVG(r.rating), 0),
			        COUNT(r),
			        com.example.app.share.Type.LESSON
			    )
			    FROM Rating r
			    JOIN r.lesson l
			    WHERE r.type = com.example.app.share.Type.LESSON
			      AND l.status = com.example.app.share.Status.PUBLISHED
			    GROUP BY l.id, l.title
			    ORDER BY
			        COALESCE(AVG(r.rating), 0) DESC,
			        COUNT(r) DESC
			""")
	List<RatingAdminResponse> getAllLessonRatingSummary();

	@Query("""
			    SELECT new com.example.app.dto.response.rating.RatingDetailAdminResponse(
			        d.id,
			        d.title,
			        SUM(CASE WHEN r.rating = 1 THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.rating = 2 THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.rating = 3 THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.rating = 4 THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.rating = 5 THEN 1 ELSE 0 END),
			        com.example.app.share.Type.DOCUMENT
			    )
			    FROM Rating r
			    JOIN r.document d
			    WHERE r.type = com.example.app.share.Type.DOCUMENT
			      AND d.id = :documentId
			    GROUP BY d.id, d.title
			""")
	RatingDetailAdminResponse getRatingDetailByDocument(@Param("documentId") Long documentId);

	@Query("""
			    SELECT new com.example.app.dto.response.rating.RatingDetailAdminResponse(
			        l.id,
			        l.title,
			        SUM(CASE WHEN r.rating = 1 THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.rating = 2 THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.rating = 3 THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.rating = 4 THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.rating = 5 THEN 1 ELSE 0 END),
			        com.example.app.share.Type.LESSON
			    )
			    FROM Rating r
			    JOIN r.lesson l
			    WHERE r.type = com.example.app.share.Type.LESSON
			      AND l.id = :lessonId
			    GROUP BY l.id, l.title
			""")
	RatingDetailAdminResponse getRatingDetailByLesson(@Param("lessonId") Long lessonId);
}
