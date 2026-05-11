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
import com.example.app.model.Document;
import com.example.app.model.DocumentRating;
import com.example.app.model.User;

@Repository
public interface DocumentRatingRepository extends JpaRepository<DocumentRating, Long> {
	List<DocumentRating> findByDocument_Id(Long documentId);

	boolean existsByUserAndDocument(User user, Document document);

	void deleteByDocument_Id(Long documentId);

	@Query("""
			    SELECT new com.example.app.dto.response.rating.RatingSummaryResponse(
			        AVG(r.rating),
			        COUNT(r.id)
			    )
			    FROM DocumentRating r
			    WHERE r.document.id = :documentId
			""")
	RatingSummaryResponse getDocumentRatingSummary(@Param("documentId") Long documentId);

	@Query("""
			    SELECT COALESCE(r.rating, 0)
			    FROM DocumentRating r
			    WHERE r.document.id = :documentId
			    AND r.user.id = :userId
			""")
	Integer getMyDocumentRating(@Param("documentId") Long documentId, @Param("userId") Long userId);

	@Query("""
			    SELECT new com.example.app.dto.response.rating.RatingAdminResponse(
			        d.id,
			        d.title,
			        COALESCE(AVG(r.rating), 0),
			        COUNT(r)
			    )
			    FROM DocumentRating r
			    JOIN r.document d
			    WHERE d.status = :status
			    GROUP BY d.id, d.title
			    ORDER BY
			        COALESCE(AVG(r.rating), 0) DESC,
			        COUNT(r) DESC
			""")
	List<RatingAdminResponse> getAllDocumentRatingSummary(@Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.rating.RatingDetailAdminResponse(
			        d.id,
			        d.title,
			        SUM(CASE WHEN r.rating = 1 THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.rating = 2 THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.rating = 3 THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.rating = 4 THEN 1 ELSE 0 END),
			        SUM(CASE WHEN r.rating = 5 THEN 1 ELSE 0 END)
			    )
			    FROM DocumentRating r
			    JOIN r.document d
			    WHERE d.id = :documentId
			    GROUP BY d.id, d.title
			""")
	RatingDetailAdminResponse getDocumentRatingDetail(@Param("documentId") Long documentId);
}
