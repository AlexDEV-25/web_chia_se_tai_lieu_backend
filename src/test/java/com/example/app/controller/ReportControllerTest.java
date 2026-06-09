package com.example.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.app.constant.InteractionType;
import com.example.app.dto.request.ReportRequest;
import com.example.app.dto.response.report.ReportUserResponse;
import com.example.app.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@DisplayName("ReportController Tests")
class ReportControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ReportService reportService;

	@Autowired
	private ObjectMapper objectMapper;

	private ReportUserResponse reportUserResponse;
	private ReportRequest reportRequest;

	@BeforeEach
	void setUp() {
		reportUserResponse = ReportUserResponse.builder().id(1L).reason("Inappropriate content").build();

		reportRequest = ReportRequest.builder().reason("Inappropriate content").contentId(1L)
				.type(InteractionType.DOCUMENT).build();
	}

	@Test
	@DisplayName("POST /api/reports - Should report content successfully")
	@WithMockUser(authorities = "REPORT")
	void testReport_Success() throws Exception {
		when(reportService.report(any(ReportRequest.class))).thenReturn(reportUserResponse);

		mockMvc.perform(post("/api/reports").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reportRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.id").value(1L))
				.andExpect(jsonPath("$.result.reason").value("Inappropriate content"));
	}

	@Test
	@DisplayName("POST /api/reports - Should handle validation errors")
	@WithMockUser(authorities = "REPORT")
	void testReport_InvalidRequest() throws Exception {
		ReportRequest invalidRequest = ReportRequest.builder().reason("") // Empty reason
				.contentId(1L).type(InteractionType.DOCUMENT).build();

		mockMvc.perform(post("/api/reports").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/reports - Should report lesson content")
	@WithMockUser(authorities = "REPORT")
	void testReport_LessonContent() throws Exception {
		ReportRequest lessonReport = ReportRequest.builder().reason("Offensive language").contentId(1L)
				.type(InteractionType.LESSON).build();

		ReportUserResponse lessonReportResponse = ReportUserResponse.builder().id(2L).reason("Offensive language")
				.build();

		when(reportService.report(any(ReportRequest.class))).thenReturn(lessonReportResponse);

		mockMvc.perform(post("/api/reports").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(lessonReport))).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.reason").value("Offensive language"));
	}
}
