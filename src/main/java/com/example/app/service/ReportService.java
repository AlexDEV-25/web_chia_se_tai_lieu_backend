package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.ReportRequest;
import com.example.app.dto.response.ReportResponse;
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

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReportService {
	private final ReportRepository reportRepository;
	private final DocumentRepository documentRepository;
	private final LessonRepository lessonRepository;
	private final ReportMapper reportMapper;
	private final GetUserByToken getUserByToken;

	@PreAuthorize("hasAuthority('REPORT_DOCUMENT')")
	public ReportResponse documentReport(ReportRequest dto) {
		Report report = new Report();
		Document doc = documentRepository.findById(dto.getContentId())
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		User user = getUserByToken.get();

		if (reportRepository.existsByUserAndDocument(user, doc)) {
			throw new AppException("Bạn đã report rồi", 1001, HttpStatus.BAD_REQUEST);
		}
		report.setCreatedAt(LocalDateTime.now());
		report.setDocument(doc);
		report.setUser(user);
		report.setReason(dto.getReason());
		report.setType(dto.getType());
		Report saved = reportRepository.save(report);
		ReportResponse response = reportMapper.reportDocumentToResponse(saved);
		return response;
	}

	@PreAuthorize("hasAuthority('REPORT_LESSON')")
	public ReportResponse lessonReport(ReportRequest dto) {
		Report report = new Report();
		Lesson lesson = lessonRepository.findById(dto.getContentId())
				.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		User user = getUserByToken.get();

		if (reportRepository.existsByUserAndLesson(user, lesson)) {
			throw new AppException("Bạn đã report rồi", 1001, HttpStatus.BAD_REQUEST);
		}
		report.setCreatedAt(LocalDateTime.now());
		report.setLesson(lesson);
		report.setUser(user);
		report.setReason(dto.getReason());
		report.setType(dto.getType());
		Report saved = reportRepository.save(report);
		ReportResponse response = reportMapper.reportDocumentToResponse(saved);
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<ReportResponse> findByDocumentId(Long documentId) {
		List<Report> reports = reportRepository.findByDocument_Id(documentId);
		List<ReportResponse> response = new ArrayList<ReportResponse>();
		for (Report r : reports) {
			response.add(reportMapper.reportDocumentToResponse(r));
		}
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<ReportResponse> findByLessonId(Long lessonId) {
		List<Report> reports = reportRepository.findByLesson_Id(lessonId);
		List<ReportResponse> response = new ArrayList<ReportResponse>();
		for (Report r : reports) {
			response.add(reportMapper.reportLessonToResponse(r));
		}
		return response;
	}
}
