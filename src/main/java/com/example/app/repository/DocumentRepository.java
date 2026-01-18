package com.example.app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.app.dto.response.CategoryCountResponse;
import com.example.app.dto.response.DailyCountResponse;
import com.example.app.model.Document;

import feign.Param;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
	List<Document> findByCategoryId(Long categoryId);

	Document findByFileUrl(String FileUrl);

	List<Document> findByUserId(Long userId);

	Optional<Document> findByIdAndUserId(Long id, Long userId);

	boolean existsById(Long id);

	@Query("""
				SELECT new com.example.app.dto.response.DailyCountResponse(
			    CAST(FUNCTION('date', d.createdAt) AS java.time.LocalDate),
			    COUNT(d)
			)
				FROM Document d
				WHERE d.status = 'PUBLISHED'
				AND (d.hide = false OR d.hide IS NULL)
				AND d.createdAt >= :fromDate
				GROUP BY FUNCTION('date', d.createdAt)
				ORDER BY FUNCTION('date', d.createdAt)
			""")
	List<DailyCountResponse> countDocumentByDay(@Param("fromDate") LocalDateTime fromDate);

	@Query("""
				SELECT new com.example.app.dto.response.CategoryCountResponse(
			    c.id,
			    c.name,
			    COUNT(d)
			)
				FROM Document d
				JOIN d.category c
				WHERE d.status = 'PUBLISHED'
				AND (d.hide = false OR d.hide IS NULL)
				GROUP BY c.id, c.name
			""")
	List<CategoryCountResponse> countDocumentByCategory();
}
