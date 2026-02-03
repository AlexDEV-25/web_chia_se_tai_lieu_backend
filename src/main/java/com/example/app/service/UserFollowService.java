package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.response.UserFollowResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.UserFollowMapper;
import com.example.app.model.User;
import com.example.app.model.UserFollow;
import com.example.app.repository.UserFollowRepository;
import com.example.app.repository.UserRepository;
import com.example.app.share.GetUserByToken;
import com.example.app.share.NotificationType;
import com.example.app.share.SendNotification;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserFollowService {
	private final UserFollowRepository userFollowRepository;
	private final UserRepository userRepository;
	private final UserFollowMapper userFollowMapper;
	private final GetUserByToken getUserByToken;
	private final SendNotification sendNotification;

	@PreAuthorize("hasAuthority('FOLLOW')")
	public UserFollowResponse save(Long request) {
		UserFollow userFollow = new UserFollow();
		User follower = getUserByToken.get();
		User following = userRepository.findById(request)
				.orElseThrow(() -> new AppException("người nhận không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		if (userFollowRepository.existsByFollowerAndFollowing(follower, following)) {
			throw new AppException("đã kết bạn rồi", 1001, HttpStatus.BAD_REQUEST);
		}
		if (follower.getId().equals(following.getId())) {
			throw new AppException("không thể follow chính mình", 1001, HttpStatus.BAD_REQUEST);
		}
		userFollow.setFollower(follower);
		userFollow.setFollowing(following);
		userFollow.setCreatedAt(LocalDateTime.now());
		UserFollow saved = userFollowRepository.save(userFollow);
		UserFollowResponse response = userFollowMapper.userFollowToResponse(saved);
		if (response != null) {

			Long NotificationId = sendNotification.saveNotification(
					"người dùng \" " + follower.getUsername() + "\" đã theo dõi bạn", NotificationType.INFO);

			if (sendNotification.saveUserNotification(follower.getId(), following.getId(), NotificationId) == false) {
				throw new AppException("Gửi thông báo không thành công", 1001, HttpStatus.BAD_REQUEST);
			}
		}
		return response;
	}

	@PreAuthorize("hasAuthority('UNFOLLOW')")
	public void delete(Long id) {
		try {
			userFollowRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new AppException("follow không tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAuthority('GET_LIST_FOLLOWING')")
	public List<UserFollowResponse> getFollowingByFollower() {
		User user = getUserByToken.get();
		List<UserFollow> userFollows = userFollowRepository.findByFollowerId(user.getId());
		List<UserFollowResponse> response = new ArrayList<UserFollowResponse>();
		for (UserFollow u : userFollows) {
			response.add(userFollowMapper.userFollowToResponse(u));
		}
		return response;
	}

	@PreAuthorize("hasAuthority('GET_LIST_FOLLOWER')")
	public List<UserFollowResponse> getFollowerByFollowing() {
		User user = getUserByToken.get();
		List<UserFollow> userFollows = userFollowRepository.findByFollowingId(user.getId());
		List<UserFollowResponse> response = new ArrayList<UserFollowResponse>();
		for (UserFollow u : userFollows) {
			response.add(userFollowMapper.userFollowToResponse(u));
		}
		return response;
	}
}
