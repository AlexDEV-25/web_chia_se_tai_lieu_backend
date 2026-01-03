package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.app.dto.request.CommentDocumentRequest;
import com.example.app.dto.request.CommentLessonRequest;
import com.example.app.dto.response.CommentDocumentResponse;
import com.example.app.dto.response.CommentLessonResponse;
import com.example.app.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "document", ignore = true)
	@Mapping(target = "lesson", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	Comment commentDocumentRequestToComment(CommentDocumentRequest request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "document", ignore = true)
	@Mapping(target = "lesson", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	Comment commentLessonRequestToComment(CommentLessonRequest request);

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "user.username", target = "username")
	@Mapping(source = "user.avatarUrl", target = "userAvatar")
	@Mapping(source = "document.id", target = "documentId")
	CommentDocumentResponse commentToCommentDocumentResponse(Comment entity);

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "user.username", target = "username")
	@Mapping(source = "user.avatarUrl", target = "userAvatar")
	@Mapping(source = "lesson.id", target = "lessonId")
	CommentLessonResponse commentToCommentLessonResponse(Comment entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "idParent", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "document", ignore = true)
	@Mapping(target = "lesson", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "hide", ignore = true)
	void updateCommentDocument(@MappingTarget Comment entity, CommentDocumentRequest request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "idParent", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "document", ignore = true)
	@Mapping(target = "lesson", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "hide", ignore = true)
	void updateCommentLesson(@MappingTarget Comment entity, CommentLessonRequest request);
}
