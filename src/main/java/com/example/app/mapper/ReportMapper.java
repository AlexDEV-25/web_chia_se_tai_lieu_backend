package com.example.app.mapper;

import org.mapstruct.Mapper;

import com.example.app.dto.response.ReportResponse;
import com.example.app.model.Report;

@Mapper(componentModel = "spring")
public interface ReportMapper {

	ReportResponse reportDocumentToResponse(Report entity);

	ReportResponse reportLessonToResponse(Report entity);
}
