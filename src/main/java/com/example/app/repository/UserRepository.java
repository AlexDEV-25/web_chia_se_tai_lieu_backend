package com.example.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.app.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);

	User findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	boolean existsById(Long id);

	boolean existsByAvatarUrl(String avatarUrl);
}
