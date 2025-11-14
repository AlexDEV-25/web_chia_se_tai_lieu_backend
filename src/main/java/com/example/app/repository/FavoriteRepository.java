package com.example.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.app.model.Document;
import com.example.app.model.Favorite;
import com.example.app.model.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

	List<Favorite> findByUser(User user);

	List<Favorite> findByDocument(Document document);

	Optional<Favorite> findByUserAndDocument(User user, Document document);

	boolean existsByUserAndDocument(User user, Document document);
}
