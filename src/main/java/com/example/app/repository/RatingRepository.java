package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.app.dto.response.RatingSummaryResponse;
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
			    SELECT new com.example.app.dto.response.RatingSummaryResponse(
			        AVG(r.rating),
			        COUNT(r.id)
			    )
			    FROM Rating r
			    WHERE r.document.id = :documentId
			""")
	RatingSummaryResponse getRatingSummaryDocument(@Param("documentId") Long documentId);

	@Query("""
			    SELECT new com.example.app.dto.response.RatingSummaryResponse(
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
}
