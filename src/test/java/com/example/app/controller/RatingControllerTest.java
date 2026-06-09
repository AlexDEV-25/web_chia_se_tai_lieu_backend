package com.example.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.example.app.dto.request.RatingRequest;
import com.example.app.dto.response.rating.RatingSummaryResponse;
import com.example.app.dto.response.rating.RatingUserResponse;
import com.example.app.service.RatingService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@DisplayName("RatingController Tests")
class RatingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RatingService ratingService;

	@Autowired
	private ObjectMapper objectMapper;

	private RatingSummaryResponse ratingSummaryResponse;
	private RatingUserResponse ratingUserResponse;
	private RatingRequest ratingRequest;

	@BeforeEach
	void setUp() {
		ratingSummaryResponse = RatingSummaryResponse.builder().average(4.5).total(100L).build();

		ratingUserResponse = RatingUserResponse.builder().rating(5).build();

		ratingRequest = RatingRequest.builder().rating(5).contentId(1L).type(InteractionType.DOCUMENT).build();
	}

	@Test
	@DisplayName("GET /api/ratings/document-summary/{documentId} - Should get document rating summary")
	void testGetRatingSummaryDocument_Success() throws Exception {
		when(ratingService.getRatingSummaryDocument(anyLong())).thenReturn(ratingSummaryResponse);

		mockMvc.perform(get("/api/ratings/document-summary/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.average").value(4.5)).andExpect(jsonPath("$.result.total").value(100L));
	}

	@Test
	@DisplayName("GET /api/ratings/lesson-summary/{lessonId} - Should get lesson rating summary")
	void testGetRatingSummaryLesson_Success() throws Exception {
		when(ratingService.getRatingSummaryLesson(anyLong())).thenReturn(ratingSummaryResponse);

		mockMvc.perform(get("/api/ratings/lesson-summary/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.average").value(4.5)).andExpect(jsonPath("$.result.total").value(100L));
	}

	@Test
	@DisplayName("GET /api/ratings/document/my-rating/{documentId} - Should get user's rating for document")
	@WithMockUser(authorities = "GET_MY_DOCUMENT_RATING")
	void testGetMyRatingDocument_Success() throws Exception {
		when(ratingService.getMyRatingDocument(anyLong())).thenReturn(5);

		mockMvc.perform(get("/api/ratings/document/my-rating/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value(5));
	}

	@Test
	@DisplayName("GET /api/ratings/document/my-rating/{documentId} - Should return 0 when no rating")
	@WithMockUser(authorities = "GET_MY_DOCUMENT_RATING")
	void testGetMyRatingDocument_NoRating() throws Exception {
		when(ratingService.getMyRatingDocument(anyLong())).thenReturn(0);

		mockMvc.perform(get("/api/ratings/document/my-rating/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value(0));
	}

	@Test
	@DisplayName("GET /api/ratings/lesson/my-rating/{lessonId} - Should get user's rating for lesson")
	@WithMockUser(authorities = "GET_MY_LESSON_RATING")
	void testGetMyRatingLesson_Success() throws Exception {
		when(ratingService.getMyRatingLesson(anyLong())).thenReturn(4);

		mockMvc.perform(get("/api/ratings/lesson/my-rating/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value(4));
	}

	@Test
	@DisplayName("POST /api/ratings - Should create rating successfully")
	@WithMockUser(authorities = "POST_RATING")
	void testCreateRating_Success() throws Exception {
		when(ratingService.saveRating(any(RatingRequest.class))).thenReturn(ratingUserResponse);

		mockMvc.perform(post("/api/ratings").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ratingRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.rating").value(5));
	}

	@Test
	@DisplayName("POST /api/ratings - Should handle invalid rating values")
	@WithMockUser(authorities = "POST_RATING")
	void testCreateRating_InvalidRating() throws Exception {
		RatingRequest invalidRequest = RatingRequest.builder().rating(10) // Invalid rating value
				.contentId(1L).type(InteractionType.DOCUMENT).build();

		mockMvc.perform(post("/api/ratings").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))).andExpect(status().isBadRequest());
	}
}
