package com.example.app.service;

import java.time.LocalDateTime;

import org.springframework.dao.EmptyResultDataAccessException;
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
	private final ReportMapper ReportMapper;
	private final GetUserByToken getUserByToken;

	@PreAuthorize("hasAuthority('REPORT_DOCUMENT')")
	public ReportResponse documentReport(ReportRequest dto) {
		Report report = new Report();
		Document doc = documentRepository.findById(dto.getContentId())
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		User user = getUserByToken.get();

		if (reportRepository.existsByUserAndDocument(user, doc)) {
			throw new AppException("đã có trong kho Report", 1001, HttpStatus.BAD_REQUEST);
		}
		report.setCreatedAt(LocalDateTime.now());
		report.setDocument(doc);
		report.setUser(user);
		report.setReason(dto.getReason());
		report.setType(dto.getType());
		Report saved = reportRepository.save(report);
		ReportResponse response = ReportMapper.reportDocumentToResponse(saved);
		return response;
	}

	@PreAuthorize("hasAuthority('REPORT_LESSON')")
	public ReportResponse lessonReport(ReportRequest dto) {
		Report report = new Report();
		Lesson lesson = lessonRepository.findById(dto.getContentId())
				.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		User user = getUserByToken.get();

		if (reportRepository.existsByUserAndLesson(user, lesson)) {
			throw new AppException("đã có trong kho Report", 1001, HttpStatus.BAD_REQUEST);
		}
		report.setCreatedAt(LocalDateTime.now());
		report.setLesson(lesson);
		report.setUser(user);
		report.setReason(dto.getReason());
		report.setType(dto.getType());
		Report saved = reportRepository.save(report);
		ReportResponse response = ReportMapper.reportDocumentToResponse(saved);
		return response;
	}

	@PreAuthorize("hasAuthority('UNREPORT_DOCUMENT')")
	public void unReportDocument(Long id) {
		try {
			reportRepository.deleteByDocument_Id(id);
		} catch (EmptyResultDataAccessException e) {
			throw new AppException("Report không tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAuthority('UNREPORT_LESSON')")
	public void unReportLesson(Long id) {
		try {
			reportRepository.deleteByLesson_Id(id);
		} catch (EmptyResultDataAccessException e) {
			throw new AppException("Report không tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
	}
}
