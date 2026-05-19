package com.example.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.constant.AppError;
import com.example.app.constant.ContentStatus;
import com.example.app.constant.InteractionType;
import com.example.app.dto.request.ReportRequest;
import com.example.app.dto.response.report.ReportAdminResponse;
import com.example.app.dto.response.report.ReportDetailAdminResponse;
import com.example.app.dto.response.report.ReportUserResponse;
import com.example.app.exception.AppException;
import com.example.app.helper.GetUserByToken;
import com.example.app.mapper.ReportMapper;
import com.example.app.model.Document;
import com.example.app.model.DocumentReport;
import com.example.app.model.Lesson;
import com.example.app.model.LessonReport;
import com.example.app.model.User;
import com.example.app.repository.DocumentReportRepository;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.LessonReportRepository;
import com.example.app.repository.LessonRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReportService {
	private final DocumentReportRepository documentReportRepository;
	private final LessonReportRepository lessonReportRepository;
	private final DocumentRepository documentRepository;
	private final LessonRepository lessonRepository;
	private final ReportMapper reportMapper;
	private final GetUserByToken getUserByToken;

	@PreAuthorize("hasRole('ADMIN')")
	public List<ReportDetailAdminResponse> findByDocumentId(Long documentId) {
		List<DocumentReport> reports = documentReportRepository.findByDocument_Id(documentId);
		List<ReportDetailAdminResponse> response = reports.stream().map(reportMapper::documentReportToResponse)
				.toList();
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<ReportDetailAdminResponse> findByLessonId(Long lessonId) {
		List<LessonReport> reports = lessonReportRepository.findByLesson_Id(lessonId);
		List<ReportDetailAdminResponse> response = reports.stream().map(reportMapper::lessonReportToResponse).toList();
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<ReportAdminResponse> getAllDocumentReportSummary() {
		return documentReportRepository.getAllDocumentReportSummary(ContentStatus.PUBLISHED);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<ReportAdminResponse> getAllLessonReportSummary() {
		return lessonReportRepository.getAllLessonReportSummary(ContentStatus.PUBLISHED);
	}

	@PreAuthorize("hasAuthority('REPORT')")
	public ReportUserResponse report(ReportRequest request) {
		User user = getUserByToken.get();

		if (request.getType() == InteractionType.DOCUMENT) {
			return saveDocumentReport(user, request);
		} else if (request.getType() == InteractionType.LESSON) {
			return saveLessonReport(user, request);
		} else {
			throw AppException.builder().appError(AppError.TYPE_NOT_FOUND).build();
		}
	}

	private ReportUserResponse saveDocumentReport(User user, ReportRequest request) {
		DocumentReport report = DocumentReport.builder().reason(request.getReason()).user(user)
				.createdAt(LocalDateTime.now()).build();
		Document doc = documentRepository.findById(request.getContentId())
				.orElseThrow(() -> AppException.builder().appError(AppError.DOCUMENT_NOT_FOUND).build());
		if (documentReportRepository.existsByUserAndDocument(user, doc)) {
			throw AppException.builder().appError(AppError.ALREADY_REPORTED).build();
		}
		report.setDocument(doc);

		DocumentReport saved = documentReportRepository.save(report);
		return reportMapper.documentReportToReportUserResponse(saved);
	}

	private ReportUserResponse saveLessonReport(User user, ReportRequest request) {
		LessonReport report = LessonReport.builder().reason(request.getReason()).user(user)
				.createdAt(LocalDateTime.now()).build();
		Lesson lesson = lessonRepository.findById(request.getContentId())
				.orElseThrow(() -> AppException.builder().appError(AppError.LECTURE_NOT_FOUND).build());
		if (lessonReportRepository.existsByUserAndLesson(user, lesson)) {
			throw AppException.builder().appError(AppError.ALREADY_REPORTED).build();
		}
		report.setLesson(lesson);

		LessonReport saved = lessonReportRepository.save(report);
		return reportMapper.lessonReportToReportUserResponse(saved);
	}
}
