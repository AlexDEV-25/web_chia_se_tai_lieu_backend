package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.app.dto.request.CommentRequest;
import com.example.app.dto.response.CommentResponse;
import com.example.app.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "document", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	Comment requestToComment(CommentRequest request);

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "user.username", target = "username")
	@Mapping(source = "user.avatarUrl", target = "userAvatar")
	@Mapping(source = "document.id", target = "documentId")
	CommentResponse commentToResponse(Comment entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "idParent", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "document", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "hide", ignore = true)
	void updateComment(@MappingTarget Comment entity, CommentRequest request);
}
