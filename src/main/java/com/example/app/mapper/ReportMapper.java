package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.app.dto.request.ReportRequest;
import com.example.app.dto.response.report.ReportDetailAdminResponse;
import com.example.app.dto.response.report.ReportUserResponse;
import com.example.app.model.Report;

@Mapper(componentModel = "spring")
public interface ReportMapper {

	@Mapping(source = "user.username", target = "username")
	@Mapping(source = "document.id", target = "contentId")
	@Mapping(source = "document.title", target = "title")
	ReportDetailAdminResponse reportDocumentToResponse(Report entity);

	@Mapping(source = "user.username", target = "username")
	@Mapping(source = "lesson.id", target = "contentId")
	@Mapping(source = "lesson.title", target = "title")
	ReportDetailAdminResponse reportLessonToResponse(Report entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "document", ignore = true)
	@Mapping(target = "lesson", ignore = true)
	Report reportRequestToReport(ReportRequest request);

	ReportUserResponse reportToReportUserResponse(Report entity);
}
