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
import com.example.app.dto.response.DocumentStatsResponse;
import com.example.app.model.Document;
import com.example.app.share.Status;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

	// lấy danh sách tài liệu của chính mình
	List<Document> findByUser_Id(Long userId);

	// lấy danh sách những tài liệu cùng danh mục nhưng khác với tài liệu đang
	// chọn không bị ẩn hay pending
	List<Document> findByIdNotAndCategory_IdAndStatusAndHideFalse(Long docId, Long categoryId, Status status);

	// lấy danh sách những tài liệu cùng tác giả nhưng khác với tài liệu đang
	// chọn không bị ẩn hay pending
	List<Document> findByIdNotAndUser_IdAndStatusAndHideFalse(Long docId, Long userId, Status status);

	// lấy tài liệu không bị ẩn hay pending
	Optional<Document> findByIdAndStatusAndHideFalse(Long Id, Status status);

	// lấy danh sách tài liệu không bị ẩn hay pending
	List<Document> findByStatusAndHideFalse(Status status);

	// lấy tài liệu của chính mình
	Optional<Document> findByIdAndUser_Id(Long id, Long userId);

	// lấy tài liệu không bị ẩn hay pending
	List<Document> findByIdNotAndStatusAndHideFalse(Long docId, Status status);

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

	@Query("""
			    SELECT new com.example.app.dto.response.DocumentStatsResponse(
			        COUNT(d),
			        COALESCE(SUM(d.downloadsCount), 0),
			        COALESCE(SUM(d.viewsCount), 0)
			    )
			    FROM Document d
			    WHERE d.hide = false
			      AND d.status = 'PUBLISHED'
			""")
	DocumentStatsResponse getStats();

	@Query("""
			    SELECT d
			    FROM Document d
			    WHERE d.hide = false
			      AND d.status = 'PUBLISHED'
			      AND (:categoryId IS NULL OR d.category.id = :categoryId)
			      AND (
			            :keyword IS NULL
			            OR d.title LIKE CONCAT('%', :keyword, '%')
			            OR d.description LIKE CONCAT('%', :keyword, '%')
			      )
			""")
	List<Document> search(@Param("keyword") String keyword, @Param("categoryId") Long categoryId);

}
