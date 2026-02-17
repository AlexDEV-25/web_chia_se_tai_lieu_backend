package com.example.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.model.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

	List<Favorite> findByUser_Id(Long userId);

	Optional<Favorite> findByUser_IdAndDocument_Id(Long userId, Long DocumentId);

	Optional<Favorite> findByUser_IdAndLesson_Id(Long userId, Long LessonId);

	boolean existsByUser_IdAndDocument_Id(Long userId, Long documentId);

	boolean existsByUser_IdAndLesson_Id(Long userId, Long lessonId);
}
