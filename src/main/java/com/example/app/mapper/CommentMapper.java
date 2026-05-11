package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.app.dto.request.CommentRequest;
import com.example.app.dto.response.comment.CommentResponse;
import com.example.app.dto.response.comment.CommentTreeResponse;
import com.example.app.model.DocumentComment;
import com.example.app.model.LessonComment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "user.username", target = "username")
	@Mapping(source = "user.avatarUrl", target = "userAvatar")
	@Mapping(source = "document.id", target = "contentId")
	@Mapping(source = "parent.id", target = "parentId")
	CommentResponse documentCommentToCommentResponse(DocumentComment entity);

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "user.username", target = "username")
	@Mapping(source = "user.avatarUrl", target = "userAvatar")
	@Mapping(source = "lesson.id", target = "contentId")
	@Mapping(source = "parent.id", target = "parentId")
	CommentResponse lessonCommentToCommentResponse(LessonComment entity);

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "user.username", target = "username")
	@Mapping(source = "user.avatarUrl", target = "userAvatar")
	@Mapping(source = "document.id", target = "contentId")
	@Mapping(source = "parent.id", target = "parentId")
	@Mapping(target = "children", ignore = true)
	CommentTreeResponse documentCommentToCommentTreeResponse(DocumentComment entity);

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "user.username", target = "username")
	@Mapping(source = "user.avatarUrl", target = "userAvatar")
	@Mapping(source = "lesson.id", target = "contentId")
	@Mapping(source = "parent.id", target = "parentId")
	@Mapping(target = "children", ignore = true)
	CommentTreeResponse lessonCommentToCommentTreeResponse(LessonComment entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "level", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "document", ignore = true)
	@Mapping(target = "parent", ignore = true)
	@Mapping(target = "replies", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "hide", ignore = true)
	void updateDocumentComment(@MappingTarget DocumentComment entity, CommentRequest request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "level", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "lesson", ignore = true)
	@Mapping(target = "parent", ignore = true)
	@Mapping(target = "replies", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "hide", ignore = true)
	void updateLessonComment(@MappingTarget LessonComment entity, CommentRequest request);

}
