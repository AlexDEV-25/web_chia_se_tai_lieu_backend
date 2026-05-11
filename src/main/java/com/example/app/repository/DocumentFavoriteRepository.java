package com.example.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.app.constant.ContentStatus;
import com.example.app.model.DocumentFavorite;

import feign.Param;

public interface DocumentFavoriteRepository extends JpaRepository<DocumentFavorite, Long> {
	@Query("""
			SELECT f
				FROM DocumentFavorite f
				JOIN f.document d
				WHERE
					f.user.id = :userId
					AND d.status = :status
					AND d.hide = false
			""")
	List<DocumentFavorite> findByUserIdAndDocumentFit(@Param("userId") Long userId,
			@Param("status") ContentStatus status);

	Optional<DocumentFavorite> findByUser_IdAndDocument_Id(Long userId, Long documentId);

	boolean existsByUser_IdAndDocument_Id(Long userId, Long documentId);

	void deleteByDocument_Id(Long documentId);
}
