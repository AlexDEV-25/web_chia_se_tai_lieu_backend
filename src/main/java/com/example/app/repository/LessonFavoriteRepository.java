package com.example.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.app.constant.ContentStatus;
import com.example.app.model.LessonFavorite;

import feign.Param;

public interface LessonFavoriteRepository extends JpaRepository<LessonFavorite, Long> {
	@Query("""
			SELECT f
				FROM LessonFavorite f
				JOIN f.lesson l
				WHERE
					f.user.id = :userId
					AND l.status = :status
					AND l.hide = false
			""")
	List<LessonFavorite> findByUserIdAndLessonFit(@Param("userId") Long userId, @Param("status") ContentStatus status);

	Optional<LessonFavorite> findByUser_IdAndLesson_Id(Long userId, Long LessonId);

	boolean existsByUser_IdAndLesson_Id(Long userId, Long lessonId);

	void deleteByLesson_Id(Long lessonId);
}
