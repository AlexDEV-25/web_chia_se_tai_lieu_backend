package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.app.dto.response.userfollow.UserFollowResponse;
import com.example.app.model.UserFollow;

@Mapper(componentModel = "spring")
public interface UserFollowMapper {

	@Mapping(source = "follower.id", target = "followerId")
	@Mapping(source = "follower.username", target = "followerName")
	@Mapping(source = "following.id", target = "followingId")
	@Mapping(source = "following.username", target = "followingName")
	@Mapping(source = "following.avatarUrl", target = "followingAvatar")
	UserFollowResponse userFollowToResponse(UserFollow entity);
}
