package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.app.dto.request.LessonRequest;
import com.example.app.dto.response.lesson.LessonAdminResponse;
import com.example.app.dto.response.lesson.LessonDetailResponse;
import com.example.app.dto.response.lesson.LessonUserResponse;
import com.example.app.model.Lesson;

@Mapper(componentModel = "spring")
public interface LessonMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "thumbnailUrl", ignore = true)
	@Mapping(target = "lessonUrl", ignore = true)
	@Mapping(target = "documentUrl", ignore = true)
	@Mapping(target = "subFileUrl", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "viewsCount", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "category", ignore = true)
	Lesson requestToLesson(LessonRequest request);

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "category.id", target = "categoryId")
	@Mapping(source = "category.name", target = "categoryName")
	@Mapping(source = "user.username", target = "userName")
	LessonDetailResponse lessonToLessonDetailResponse(Lesson entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "thumbnailUrl", ignore = true)
	@Mapping(target = "lessonUrl", ignore = true)
	@Mapping(target = "documentUrl", ignore = true)
	@Mapping(target = "subFileUrl", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "viewsCount", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "category", ignore = true)
	void updateLesson(@MappingTarget Lesson entity, LessonRequest request);

	@Mapping(source = "category.id", target = "categoryId")
	LessonUserResponse lessonToLessonUserResponse(Lesson entity);

	@Mapping(source = "category.name", target = "categoryName")
	LessonAdminResponse lessonToLessonAdminResponse(Lesson entity);
}
