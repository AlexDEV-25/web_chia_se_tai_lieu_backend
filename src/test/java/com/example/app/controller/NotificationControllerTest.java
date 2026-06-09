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

import com.example.app.constant.NotificationType;
import com.example.app.dto.request.NotificationRequest;
import com.example.app.dto.response.notification.NotificationResponse;
import com.example.app.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@DisplayName("NotificationController Tests")
class NotificationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private NotificationService notificationService;

	@Autowired
	private ObjectMapper objectMapper;

	private NotificationResponse notificationResponse;
	private NotificationRequest notificationRequest;

	@BeforeEach
	void setUp() {
		notificationResponse = NotificationResponse.builder().id(1L).content("This is a test notification")
				.link("/notifications/1").type(NotificationType.ERROR).build();

		notificationRequest = NotificationRequest.builder().content("This is a test notification")
				.link("/notifications/1").type(NotificationType.ERROR).build();
	}

	@Test
	@DisplayName("POST /api/notifications - Should create notification successfully")
	@WithMockUser(roles = "ADMIN")
	void testCreate_Success() throws Exception {
		when(notificationService.save(any(NotificationRequest.class))).thenReturn(notificationResponse);
		mockMvc.perform(post("/api/notifications").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(notificationRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.id").value(1L))
				.andExpect(jsonPath("$.result.content").value("This is a test notification"))
				.andExpect(jsonPath("$.result.link").value("/notifications/1"));
	}

	@Test
	@DisplayName("POST /api/notifications - Should handle validation errors")
	@WithMockUser(roles = "ADMIN")
	void testCreate_InvalidRequest() throws Exception {
		NotificationRequest invalidRequest = NotificationRequest.builder().content("") // Empty content
				.link("/notifications/1").type(NotificationType.ERROR).build();

		mockMvc.perform(post("/api/notifications").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))).andExpect(status().isBadRequest());
	}
}
