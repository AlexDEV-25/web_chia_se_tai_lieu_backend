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
import com.example.app.dto.response.DocumentFavoriteResponse;
import com.example.app.dto.response.DocumentStatsResponse;
import com.example.app.model.Document;
import com.example.app.share.Status;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

	// lấy danh sách tài liệu của chính mình
	List<Document> findByUser_Id(Long userId);

	// lấy tài liệu không bị ẩn hay pending
	Optional<Document> findByIdAndStatusAndHideFalse(Long Id, Status status);

	// lấy danh sách tài liệu không bị ẩn hay pending
	List<Document> findByStatusAndHideFalse(Status status);

	// lấy tài liệu của chính mình
	Optional<Document> findByIdAndUser_Id(Long id, Long userId);

	// lấy tài liệu không bị ẩn hay pending
	List<Document> findByIdNotAndStatusAndHideFalse(Long docId, Status status);

	// lấy số tài liệu của 1 người đã được duyệt và không bị ẩn
	long countByUser_IdAndStatusAndHideFalse(Long userId, Status status);

	// lấy số tài liệu của chính mình đăng tải lên
	long countByUser_Id(Long userId);

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
			    SELECT new com.example.app.dto.response.DocumentFavoriteResponse(
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
			    LEFT JOIN d.favorites f
			        ON f.user.id = :currentUserId
			        AND f.type = com.example.app.share.Type.DOCUMENT
			    WHERE d.hide = false
			      AND d.status = com.example.app.share.Status.PUBLISHED
			      AND (:categoryId IS NULL OR d.category.id = :categoryId)
			      AND (
			            :keyword IS NULL
			            OR d.title LIKE CONCAT('%', :keyword, '%')
			            OR d.description LIKE CONCAT('%', :keyword, '%')
			      )
			""")
	List<DocumentFavoriteResponse> searchWithFavoriteStatus(@Param("keyword") String keyword,
			@Param("categoryId") Long categoryId, @Param("currentUserId") Long currentUserId);

	@Query("""
			  SELECT new com.example.app.dto.response.DocumentFavoriteResponse(
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
			      AND d.status = 'PUBLISHED'
			      AND (:categoryId IS NULL OR d.category.id = :categoryId)
			      AND (
			            :keyword IS NULL
			            OR d.title LIKE CONCAT('%', :keyword, '%')
			            OR d.description LIKE CONCAT('%', :keyword, '%')
			      )
			""")
	List<DocumentFavoriteResponse> searchWithWithoutFavorite(@Param("keyword") String keyword,
			@Param("categoryId") Long categoryId);

	@Query("""
			    SELECT new com.example.app.dto.response.DocumentFavoriteResponse(
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
			    LEFT JOIN d.favorites f
			        ON f.document.id = d.id
			        AND f.user.id = :currentUserId
			        AND f.type = com.example.app.share.Type.DOCUMENT
			    WHERE d.user.id = :authorId
			        AND d.id <> :currentDocumentId
			        AND d.status = com.example.app.share.Status.PUBLISHED
			        AND d.hide = false
			    ORDER BY d.createdAt DESC
			""")
	List<DocumentFavoriteResponse> findDocumentsByUserWithFavoriteStatus(@Param("authorId") Long authorId,
			@Param("currentUserId") Long currentUserId, @Param("currentDocumentId") Long currentDocumentId);

	@Query("""
			    SELECT new com.example.app.dto.response.DocumentFavoriteResponse(
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
			        AND d.status = com.example.app.share.Status.PUBLISHED
			        AND d.hide = false
			    ORDER BY d.createdAt DESC
			""")
	List<DocumentFavoriteResponse> findDocumentsByUserWithoutFavorite(@Param("authorId") Long authorId,
			@Param("currentDocumentId") Long currentDocumentId);

	@Query("""
			    SELECT new com.example.app.dto.response.DocumentFavoriteResponse(
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
			    LEFT JOIN d.favorites f
			        ON f.document.id = d.id
			        AND f.user.id = :currentUserId
			        AND f.type = com.example.app.share.Type.DOCUMENT
			    WHERE d.user.id = :authorId
			        AND d.status = com.example.app.share.Status.PUBLISHED
			        AND d.hide = false
			    ORDER BY d.createdAt DESC
			""")
	List<DocumentFavoriteResponse> findAllDocumentsByUserWithFavoriteStatus(@Param("authorId") Long authorId,
			@Param("currentUserId") Long currentUserId);

	@Query("""
			    SELECT new com.example.app.dto.response.DocumentFavoriteResponse(
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
			        AND d.status = com.example.app.share.Status.PUBLISHED
			        AND d.hide = false
			    ORDER BY d.createdAt DESC
			""")
	List<DocumentFavoriteResponse> findAllDocumentsByUserWithoutFavorite(@Param("authorId") Long authorId);

	@Query("""
			    SELECT new com.example.app.dto.response.DocumentFavoriteResponse(
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
			    LEFT JOIN d.favorites f
			        ON f.document.id = d.id
			        AND f.user.id = :currentUserId
			        AND f.type = com.example.app.share.Type.DOCUMENT
			    WHERE d.category.id = :categoryId
			        AND d.id <> :currentDocumentId
			        AND d.status = com.example.app.share.Status.PUBLISHED
			        AND d.hide = false
			    ORDER BY d.createdAt DESC
			""")
	List<DocumentFavoriteResponse> findDocumentsByCategoryWithFavoriteStatus(@Param("categoryId") Long categoryId,
			@Param("currentUserId") Long currentUserId, @Param("currentDocumentId") Long currentDocumentId);

	@Query("""
			    SELECT new com.example.app.dto.response.DocumentFavoriteResponse(
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
			        AND d.status = com.example.app.share.Status.PUBLISHED
			        AND d.hide = false
			    ORDER BY d.createdAt DESC
			""")
	List<DocumentFavoriteResponse> findDocumentsByCategoryWithoutFavorite(@Param("categoryId") Long categoryId,
			@Param("currentDocumentId") Long currentDocumentId);

	@Query("""
			    SELECT new com.example.app.dto.response.DocumentFavoriteResponse(
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
			    LEFT JOIN d.favorites f
			        ON f.document.id = d.id
			        AND f.user.id = :currentUserId
			        AND f.type = com.example.app.share.Type.DOCUMENT
			    WHERE d.status = com.example.app.share.Status.PUBLISHED
			        AND d.hide = false
			    ORDER BY d.createdAt DESC
			""")
	List<DocumentFavoriteResponse> findAllWithFavoriteStatus(@Param("currentUserId") Long currentUserId);

	@Query("""
			    SELECT new com.example.app.dto.response.DocumentFavoriteResponse(
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
			    WHERE d.status = com.example.app.share.Status.PUBLISHED
			        AND d.hide = false
			    ORDER BY d.createdAt DESC
			""")
	List<DocumentFavoriteResponse> findAllWithoutFavorite();

}
