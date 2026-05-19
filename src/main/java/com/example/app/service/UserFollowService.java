package com.example.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.constant.AppError;
import com.example.app.dto.response.userfollow.FollowCountResponse;
import com.example.app.dto.response.userfollow.UserFollowResponse;
import com.example.app.event.UserFollowCreateEvent;
import com.example.app.exception.AppException;
import com.example.app.helper.GetUserByToken;
import com.example.app.mapper.UserFollowMapper;
import com.example.app.model.User;
import com.example.app.model.UserFollow;
import com.example.app.repository.UserFollowRepository;
import com.example.app.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserFollowService {
	private final UserFollowRepository userFollowRepository;
	private final UserRepository userRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final UserFollowMapper userFollowMapper;
	private final GetUserByToken getUserByToken;

	@PreAuthorize("hasAuthority('FOLLOW')")
	@Transactional
	public UserFollowResponse save(Long request) {

		User follower = getUserByToken.get();
		User following = userRepository.findById(request)
				.orElseThrow(() -> AppException.builder().appError(AppError.USER_NOT_FOUND).build());
		if (userFollowRepository.existsByFollowerAndFollowing(follower, following)) {
			throw AppException.builder().appError(AppError.ALREADY_FRIEND).build();
		}
		if (follower.getId().equals(following.getId())) {
			throw AppException.builder().appError(AppError.CANNOT_FOLLOW_YOURSELF).build();
		}
		UserFollow userFollow = UserFollow.builder().follower(follower).following(following)
				.createdAt(LocalDateTime.now()).build();
		UserFollow saved = userFollowRepository.save(userFollow);
		UserFollowResponse response = userFollowMapper.userFollowToResponse(saved);
		if (response != null) {
			eventPublisher.publishEvent(new UserFollowCreateEvent(follower, following));
		}
		return response;
	}

	@PreAuthorize("hasAuthority('UNFOLLOW')")
	@Transactional
	public void delete(Long followingId) {
		User follower = getUserByToken.get();
		User following = userRepository.findByIdAndHideFalse(followingId)
				.orElseThrow(() -> AppException.builder().appError(AppError.USER_NOT_FOUND).build());

		userFollowRepository.deleteByFollowerAndFollowing(follower, following);

	}

	@PreAuthorize("hasAuthority('GET_LIST_FOLLOWING')")
	public List<UserFollowResponse> getFollowingByFollower() {
		User user = getUserByToken.get();
		List<UserFollow> userFollows = userFollowRepository.findByFollower_Id(user.getId());
		List<UserFollowResponse> response = userFollows.stream().map(userFollowMapper::userFollowToResponse).toList();
		return response;
	}

	@PreAuthorize("hasAuthority('GET_LIST_FOLLOWER')")
	public List<UserFollowResponse> getFollowerByFollowing() {
		User user = getUserByToken.get();
		List<UserFollow> userFollows = userFollowRepository.findByFollowing_Id(user.getId());
		List<UserFollowResponse> response = userFollows.stream().map(userFollowMapper::userFollowToResponse).toList();
		return response;
	}

	@PreAuthorize("hasAuthority('GET_MY_FOLLOW_COUNT')")
	public FollowCountResponse getMyFollowCount() {
		User user = getUserByToken.get();
		Long follower = userFollowRepository.countByFollowing_Id(user.getId());
		Long following = userFollowRepository.countByFollower_Id(user.getId());
		FollowCountResponse response = new FollowCountResponse(follower, following);
		return response;
	}

	@PreAuthorize("hasAuthority('CHECK_FOLLOWED')")
	public boolean checkFollowed(Long userId) {
		User follower = getUserByToken.get();
		User following = userRepository.findByIdAndHideFalse(userId)
				.orElseThrow(() -> AppException.builder().appError(AppError.USER_NOT_FOUND).build());

		return userFollowRepository.existsByFollowerAndFollowing(follower, following);
	}

	@PreAuthorize("hasAuthority('CHECK_IS_ME')")
	public boolean checkIsMe(Long userId) {
		User follower = getUserByToken.get();
		return follower.getId() == userId;
	}

	public FollowCountResponse getFollowCount(Long userId) {
		Long follower = userFollowRepository.countByFollowing_Id(userId);
		Long following = userFollowRepository.countByFollower_Id(userId);
		FollowCountResponse response = new FollowCountResponse(follower, following);
		return response;
	}
}
