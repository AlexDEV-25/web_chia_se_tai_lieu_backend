package com.example.app.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.response.statistic.CategoryCountResponse;
import com.example.app.dto.response.statistic.DailyCountResponse;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.LessonRepository;
import com.example.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {

	private final UserRepository userRepository;
	private final DocumentRepository documentRepository;
	private final LessonRepository lessonRepository;

	@PreAuthorize("hasRole('ADMIN')")
	public List<DailyCountResponse> userLast7Days() {
		return fill7Days(userRepository.countUserByDay(LocalDate.now().minusDays(6).atStartOfDay()));
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<DailyCountResponse> documentLast7Days() {
		return fill7Days(documentRepository.countDocumentByDay(LocalDate.now().minusDays(6).atStartOfDay()));
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<CategoryCountResponse> documentByCategory() {
		return documentRepository.countDocumentByCategory();
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<CategoryCountResponse> lessonByCategory() {
		return lessonRepository.countLessonByCategory();
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<DailyCountResponse> lessonLast7Days() {
		return fill7Days(lessonRepository.countLessonByDay(LocalDate.now().minusDays(6).atStartOfDay()));
	}

	private List<DailyCountResponse> fill7Days(List<DailyCountResponse> raw) {

		LocalDate today = LocalDate.now();
		LocalDate fromDate = today.minusDays(6);

		Map<LocalDate, Long> map = raw.stream()
				.collect(Collectors.toMap(DailyCountResponse::date, DailyCountResponse::total));

		List<DailyCountResponse> result = new ArrayList<>();

		for (int i = 0; i < 7; i++) {
			LocalDate date = fromDate.plusDays(i);
			result.add(new DailyCountResponse(date, map.getOrDefault(date, 0L)));
		}

		return result;
	}
}
