package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.model.Document;
import com.example.app.model.Favorite;
import com.example.app.model.Lesson;
import com.example.app.model.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

	List<Favorite> findByUser_Id(Long userId);

	boolean existsByUserAndDocument(User user, Document document);

	boolean existsByUserAndLesson(User user, Lesson lesson);
}
