package com.example.app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.app.dto.response.statistic.DailyCountResponse;
import com.example.app.model.User;

import feign.Param;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);

	User findByEmail(String email);

	Optional<User> findByIdAndHideFalse(Long Id);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	boolean existsById(Long id);

	boolean existsByAvatarUrl(String avatarUrl);

	@Query("""
				SELECT new com.example.app.dto.response.statistic.DailyCountResponse(
			    CAST(FUNCTION('date', u.createdAt) AS java.time.LocalDate),
			    COUNT(u)
			)

				FROM User u
				WHERE (u.hide = false OR u.hide IS NULL)
				AND u.createdAt >= :fromDate
				GROUP BY FUNCTION('date', u.createdAt)
				ORDER BY FUNCTION('date', u.createdAt)
			""")
	List<DailyCountResponse> countUserByDay(@Param("fromDate") LocalDateTime fromDate);
}
