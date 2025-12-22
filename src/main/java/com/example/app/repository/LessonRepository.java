package com.example.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.model.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
	List<Lesson> findByCategoryId(Long categoryId);

	Lesson findByLessonUrl(String lessonUrl);

	List<Lesson> findByUserId(Long userId);

	Optional<Lesson> findByIdAndUserId(Long id, Long userId);

	boolean existsById(Long id);
}
