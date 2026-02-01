package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.app.dto.request.UserFollowRequest;
import com.example.app.dto.response.UserFollowResponse;
import com.example.app.model.UserFollow;

@Mapper(componentModel = "spring")
public interface UserFollowMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "follower", ignore = true)
	@Mapping(target = "following", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	UserFollow requestToUserFollow(UserFollowRequest request);

	@Mapping(source = "follower.id", target = "followerId")
	@Mapping(source = "follower.username", target = "followerName")
	@Mapping(source = "following.id", target = "followingId")
	@Mapping(source = "following.username", target = "followingName")
	@Mapping(source = "following.avatarUrl", target = "followingAvatar")
	UserFollowResponse userFollowToResponse(UserFollow entity);
}
