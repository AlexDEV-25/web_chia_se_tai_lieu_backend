package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.app.dto.response.ReportResponse;
import com.example.app.model.Report;

@Mapper(componentModel = "spring")
public interface ReportMapper {

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "user.username", target = "username")
	@Mapping(source = "document.id", target = "contentId")
	@Mapping(source = "document.title", target = "title")
	ReportResponse reportDocumentToResponse(Report entity);

	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "user.username", target = "username")
	@Mapping(source = "lesson.id", target = "contentId")
	@Mapping(source = "lesson.title", target = "title")
	ReportResponse reportLessonToResponse(Report entity);
}
