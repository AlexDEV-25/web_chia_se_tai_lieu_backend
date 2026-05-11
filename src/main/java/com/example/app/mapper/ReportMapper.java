package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.app.dto.request.ReportRequest;
import com.example.app.dto.response.report.ReportDetailAdminResponse;
import com.example.app.dto.response.report.ReportUserResponse;
import com.example.app.model.DocumentReport;
import com.example.app.model.LessonReport;

@Mapper(componentModel = "spring")
public interface ReportMapper {

	@Mapping(source = "user.username", target = "username")
	@Mapping(source = "document.id", target = "contentId")
	@Mapping(source = "document.title", target = "title")
	ReportDetailAdminResponse documentReportToResponse(DocumentReport entity);

	@Mapping(source = "user.username", target = "username")
	@Mapping(source = "lesson.id", target = "contentId")
	@Mapping(source = "lesson.title", target = "title")
	ReportDetailAdminResponse lessonReportToResponse(LessonReport entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "document", ignore = true)
	DocumentReport reportToDocumentReport(ReportRequest request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "lesson", ignore = true)
	LessonReport reportToLessonReport(ReportRequest request);

	ReportUserResponse documentReportToReportUserResponse(DocumentReport entity);

	ReportUserResponse lessonReportToReportUserResponse(LessonReport entity);
}
