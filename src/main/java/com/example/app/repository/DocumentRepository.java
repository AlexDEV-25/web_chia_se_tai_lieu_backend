package com.example.app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.app.constant.ContentStatus;
import com.example.app.dto.response.document.DocumentResponse;
import com.example.app.dto.response.document.DocumentStatsResponse;
import com.example.app.dto.response.statistic.CategoryCountResponse;
import com.example.app.model.Category;
import com.example.app.model.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

	// lấy danh sách tài liệu của chính mình
	List<Document> findByUser_Id(Long userId);

	// lấy tài liệu không bị ẩn hay pending
	Optional<Document> findByIdAndStatusAndHideFalse(Long Id, ContentStatus status);

	// lấy danh sách tài liệu không bị ẩn hay pending
	List<Document> findByStatusAndHideFalse(ContentStatus status);

	// lấy tài liệu của chính mình
	Optional<Document> findByIdAndUser_Id(Long id, Long userId);

	// lấy số tài liệu của 1 người đã được duyệt và không bị ẩn
	long countByUser_IdAndStatusAndHideFalse(Long userId, ContentStatus status);

	// lấy số tài liệu của chính mình đăng tải lên
	long countByUser_Id(Long userId);

	@Query(value = """
				SELECT DATE(d.created_at) as stat_date, COUNT(d.id)
				FROM documents d
				WHERE d.status = :status
				AND (d.hide = 0 OR d.hide IS NULL)
				AND d.created_at >= :fromDate
				GROUP BY DATE(d.created_at)
				ORDER BY DATE(d.created_at)
			""", nativeQuery = true)
	List<Object[]> countDocumentByDay(@Param("fromDate") LocalDateTime fromDate, @Param("status") ContentStatus status);

	@Query("""
				SELECT new com.example.app.dto.response.statistic.CategoryCountResponse(
			    c.id,
			    c.name,
			    COUNT(d)
			)
				FROM Document d
				JOIN  d.category c ON d.category.id = c.id
				WHERE d.status = :status
				AND (d.hide = false OR d.hide IS NULL)
				GROUP BY c.id, c.name
			""")
	List<CategoryCountResponse> countDocumentByCategory(@Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.document.DocumentStatsResponse(
			        COUNT(d),
			        COALESCE(SUM(d.downloadsCount), 0),
			        COALESCE(SUM(d.viewsCount), 0)
			    )
			    FROM Document d
			    WHERE d.hide = false
			      AND d.status = :status
			""")
	DocumentStatsResponse getStats(@Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.document.DocumentResponse(
			        d.id,
			        d.title,
			        d.description,
			        d.thumbnailUrl,
			        d.user.username,
			        d.viewsCount,
			        d.downloadsCount,
			        CASE WHEN f IS NOT NULL THEN true ELSE false END
			    )
			    FROM Document d
			    LEFT JOIN DocumentFavorite f
			        ON  f.document.id = d.id
			        AND f.user.id = :currentUserId
			    WHERE d.hide = false
			      AND d.status = :status
			      AND (:categoryId IS NULL OR d.category.id = :categoryId)
			      AND (
			            :keyword IS NULL
			            OR d.title LIKE CONCAT('%', :keyword, '%')
			            OR d.description LIKE CONCAT('%', :keyword, '%')
			      )
			""")
	List<DocumentResponse> searchWhenLogin(@Param("keyword") String keyword, @Param("categoryId") Long categoryId,
			@Param("currentUserId") Long currentUserId, @Param("status") ContentStatus status);

	@Query("""
			  SELECT new com.example.app.dto.response.document.DocumentResponse(
			        d.id,
			        d.title,
			        d.description,
			        d.thumbnailUrl,
			        d.user.username,
			        d.viewsCount,
			        d.downloadsCount,
			        false
			    )
			    FROM Document d
			    WHERE d.hide = false
			      AND d.status = :status
			      AND (:categoryId IS NULL OR d.category.id = :categoryId)
			      AND (
			            :keyword IS NULL
			            OR d.title LIKE CONCAT('%', :keyword, '%')
			            OR d.description LIKE CONCAT('%', :keyword, '%')
			      )
			""")
	List<DocumentResponse> searchWithoutLogin(@Param("keyword") String keyword, @Param("categoryId") Long categoryId,
			@Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.document.DocumentResponse(
			        d.id,
			        d.title,
			        d.description,
			        d.thumbnailUrl,
			        d.user.username,
			        d.viewsCount,
			        d.downloadsCount,
			        CASE WHEN f.id IS NOT NULL THEN true ELSE false END
			    )
			    FROM Document d
			    LEFT JOIN DocumentFavorite f
			        ON  f.document.id = d.id
			        AND f.user.id = :currentUserId
			    WHERE d.user.id = :authorId
			        AND d.id <> :currentDocumentId
			        AND d.status = :status
			        AND d.hide = false
			    ORDER BY d.createdAt DESC
			""")
	List<DocumentResponse> getByUserWhenLoginAndDifferentCurrentDocument(@Param("authorId") Long authorId,
			@Param("currentUserId") Long currentUserId, @Param("currentDocumentId") Long currentDocumentId,
			@Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.document.DocumentResponse(
			        d.id,
			        d.title,
			        d.description,
			        d.thumbnailUrl,
			        d.user.username,
			        d.viewsCount,
			        d.downloadsCount,
			        false
			    )
			    FROM Document d
			    WHERE d.user.id = :authorId
			        AND d.id <> :currentDocumentId
			        AND d.status = :status
			        AND d.hide = false
			    ORDER BY d.createdAt DESC
			""")
	List<DocumentResponse> getByUserWithoutLoginAndDifferentCurrentDocument(@Param("authorId") Long authorId,
			@Param("currentDocumentId") Long currentDocumentId, @Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.document.DocumentResponse(
			        d.id,
			        d.title,
			        d.description,
			        d.thumbnailUrl,
			        d.user.username,
			        d.viewsCount,
			        d.downloadsCount,
			        CASE WHEN f.id IS NOT NULL THEN true ELSE false END
			    )
			    FROM Document d
			     LEFT JOIN DocumentFavorite f
			        ON  f.document.id = d.id
			        AND f.user.id = :currentUserId
			    WHERE d.user.id = :authorId
			        AND d.status = :status
			        AND d.hide = false
			    ORDER BY d.createdAt DESC
			""")
	List<DocumentResponse> getByUserWhenLogin(@Param("authorId") Long authorId,
			@Param("currentUserId") Long currentUserId, @Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.document.DocumentResponse(
			        d.id,
			        d.title,
			        d.description,
			        d.thumbnailUrl,
			        d.user.username,
			        d.viewsCount,
			        d.downloadsCount,
			        false
			    )
			    FROM Document d
			    WHERE d.user.id = :authorId
			        AND d.status = :status
			        AND d.hide = false
			    ORDER BY d.createdAt DESC
			""")
	List<DocumentResponse> getByUserWithoutLogin(@Param("authorId") Long authorId,
			@Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.document.DocumentResponse(
			        d.id,
			        d.title,
			        d.description,
			        d.thumbnailUrl,
			        d.user.username,
			        d.viewsCount,
			        d.downloadsCount,
			        CASE WHEN f.id IS NOT NULL THEN true ELSE false END
			    )
			    FROM Document d
			    LEFT JOIN DocumentFavorite f
			        ON  f.document.id = d.id
			        AND f.user.id = :currentUserId
			    WHERE d.category.id = :categoryId
			        AND d.id <> :currentDocumentId
			        AND d.status = :status
			        AND d.hide = false
			    ORDER BY d.createdAt DESC
			""")
	List<DocumentResponse> getByCategoryWhenLoginAndDifferentCurrentDocument(@Param("categoryId") Long categoryId,
			@Param("currentUserId") Long currentUserId, @Param("currentDocumentId") Long currentDocumentId,
			@Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.document.DocumentResponse(
			        d.id,
			        d.title,
			        d.description,
			        d.thumbnailUrl,
			        d.user.username,
			        d.viewsCount,
			        d.downloadsCount,
			        false
			    )
			    FROM Document d
			    WHERE d.category.id = :categoryId
			        AND d.id <> :currentDocumentId
			        AND d.status = :status
			        AND d.hide = false
			    ORDER BY d.createdAt DESC
			""")
	List<DocumentResponse> getByCategoryWithoutLoginAndDifferentCurrentDocument(@Param("categoryId") Long categoryId,
			@Param("currentDocumentId") Long currentDocumentId, @Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.document.DocumentResponse(
			        d.id,
			        d.title,
			        d.description,
			        d.thumbnailUrl,
			        d.user.username,
			        d.viewsCount,
			        d.downloadsCount,
			        CASE WHEN f.id IS NOT NULL THEN true ELSE false END
			    )
			    FROM Document d
			    LEFT JOIN DocumentFavorite f
			        ON  f.document.id = d.id
			        AND f.user.id = :currentUserId
			    WHERE d.status = :status
			        AND d.hide = false
			    ORDER BY d.createdAt DESC
			""")
	List<DocumentResponse> getAllWhenLogin(@Param("currentUserId") Long currentUserId,
			@Param("status") ContentStatus status);

	@Query("""
			    SELECT new com.example.app.dto.response.document.DocumentResponse(
			        d.id,
			        d.title,
			        d.description,
			        d.thumbnailUrl,
			        d.user.username,
			        d.viewsCount,
			        d.downloadsCount,
			        false
			    )
			    FROM Document d
			    WHERE d.status = :status
			        AND d.hide = false
			    ORDER BY d.createdAt DESC
			""")
	List<DocumentResponse> getAllWithoutLogin(@Param("status") ContentStatus status);

	List<Document> findByCategoryAndStatusAndHideFalse(Category category, ContentStatus status);

}
