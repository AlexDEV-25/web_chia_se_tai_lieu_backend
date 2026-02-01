package com.example.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.app.model.User;
import com.example.app.model.UserFollow;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
	boolean existsByFollowerAndFollowing(User Follow, User Following);

	List<UserFollow> findByFollowerId(Long followerId);

	List<UserFollow> findByFollowingId(Long followingId);
}
