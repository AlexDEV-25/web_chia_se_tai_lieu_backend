package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.app.dto.request.UserRequest;
import com.example.app.dto.response.UserResponse;
import com.example.app.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "activationCode", ignore = true)
	@Mapping(target = "avatarUrl", ignore = true)
	@Mapping(target = "documents", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "comments", ignore = true)
	@Mapping(target = "ratings", ignore = true)
	@Mapping(target = "favorites", ignore = true)
	@Mapping(target = "roles", ignore = true)
	User requestToUser(UserRequest request);

	UserResponse userToResponse(User entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "avatarUrl", ignore = true)
	@Mapping(target = "email", ignore = true)
	@Mapping(target = "username", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "activationCode", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "documents", ignore = true)
	@Mapping(target = "comments", ignore = true)
	@Mapping(target = "ratings", ignore = true)
	@Mapping(target = "favorites", ignore = true)
	@Mapping(target = "roles", ignore = true)
	void updateUser(@MappingTarget User user, UserRequest request);
}
