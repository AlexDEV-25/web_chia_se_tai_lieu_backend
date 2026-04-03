package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.ReportRequest;
import com.example.app.dto.response.report.ReportAdminResponse;
import com.example.app.dto.response.report.ReportDetailAdminResponse;
import com.example.app.dto.response.report.ReportUserResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.ReportMapper;
import com.example.app.model.Document;
import com.example.app.model.Lesson;
import com.example.app.model.Report;
import com.example.app.model.User;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.LessonRepository;
import com.example.app.repository.ReportRepository;
import com.example.app.share.GetUserByToken;
import com.example.app.share.Type;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReportService {
	private final ReportRepository reportRepository;
	private final DocumentRepository documentRepository;
	private final LessonRepository lessonRepository;
	private final ReportMapper reportMapper;
	private final GetUserByToken getUserByToken;

	@PreAuthorize("hasRole('ADMIN')")
	public List<ReportDetailAdminResponse> findByDocumentId(Long documentId) {
		List<Report> reports = reportRepository.findByDocument_Id(documentId);
		List<ReportDetailAdminResponse> response = new ArrayList<ReportDetailAdminResponse>();
		for (Report r : reports) {
			response.add(reportMapper.reportDocumentToResponse(r));
		}
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<ReportDetailAdminResponse> findByLessonId(Long lessonId) {
		List<Report> reports = reportRepository.findByLesson_Id(lessonId);
		List<ReportDetailAdminResponse> response = new ArrayList<ReportDetailAdminResponse>();
		for (Report r : reports) {
			response.add(reportMapper.reportLessonToResponse(r));
		}
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<ReportAdminResponse> getAllDocumentReportSummary() {
		List<ReportAdminResponse> response = reportRepository.getAllDocumentReportSummary();
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<ReportAdminResponse> getAllLessonReportSummary() {
		List<ReportAdminResponse> response = reportRepository.getAllLessonReportSummary();
		return response;
	}

	@PreAuthorize("hasAuthority('REPORT')")
	public ReportUserResponse report(ReportRequest dto) {
		User user = getUserByToken.get();
		Report report = reportMapper.reportRequestToReport(dto);

		if (report.getType() == Type.DOCUMENT) {
			Document doc = documentRepository.findById(dto.getContentId())
					.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));
			if (reportRepository.existsByUserAndDocument(user, doc)) {
				throw new AppException("Bạn đã report rồi", 1001, HttpStatus.BAD_REQUEST);
			}
			report.setDocument(doc);
		} else {
			Lesson lesson = lessonRepository.findById(dto.getContentId())
					.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));
			if (reportRepository.existsByUserAndLesson(user, lesson)) {
				throw new AppException("Bạn đã report rồi", 1001, HttpStatus.BAD_REQUEST);
			}
			report.setLesson(lesson);
		}

		report.setCreatedAt(LocalDateTime.now());
		report.setUser(user);

		Report saved = reportRepository.save(report);
		ReportUserResponse response = reportMapper.reportToReportUserResponse(saved);
		return response;
	}
}
