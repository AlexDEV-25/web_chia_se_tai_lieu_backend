package com.example.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

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
import com.example.app.dto.request.FavoriteRequest;
import com.example.app.dto.response.favorite.FavoriteResponse;
import com.example.app.service.FavoriteService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@DisplayName("FavoriteController Tests")
class FavoriteControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FavoriteService favoriteService;

	@Autowired
	private ObjectMapper objectMapper;

	private FavoriteResponse favoriteResponse;
	private FavoriteRequest favoriteRequest;

	@BeforeEach
	void setUp() {
		favoriteResponse = FavoriteResponse.builder().id(1L).contentId(1L).title("Test Document")
				.thumbnailUrl("http://example.com/thumb.jpg").authorName("Test User").build();

		favoriteRequest = FavoriteRequest.builder().contentId(1L).type(InteractionType.DOCUMENT).build();
	}

	@Test
	@DisplayName("POST /api/favorites - Should add favorite successfully")
	@WithMockUser(authorities = "ADD_FAVORITE")
	void testAddFavorite_Success() throws Exception {
		when(favoriteService.addFavorite(any(FavoriteRequest.class))).thenReturn(favoriteResponse);

		mockMvc.perform(post("/api/favorites").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(favoriteRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.id").value(1L)).andExpect(jsonPath("$.result.contentId").value(1L));
	}

	@Test
	@DisplayName("DELETE /api/favorites/document/{documentId} - Should remove document favorite")
	@WithMockUser(authorities = "REMOVE_DOCUMENT_FAVORITE")
	void testRemoveDocumentFavorite_Success() throws Exception {
		doNothing().when(favoriteService).removeDocumentFavorite(anyLong());

		mockMvc.perform(delete("/api/favorites/document/1")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("DELETE /api/favorites/lesson/{lessonId} - Should remove lesson favorite")
	@WithMockUser(authorities = "REMOVE_LESSON_FAVORITE")
	void testRemoveLessonFavorite_Success() throws Exception {
		doNothing().when(favoriteService).removeLessonFavorite(anyLong());

		mockMvc.perform(delete("/api/favorites/lesson/1")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET /api/favorites/document/user - Should get document favorites by user")
	@WithMockUser(authorities = "GET_DOCUMENT_FAVORITE")
	void testGetDocumentFavoritesByUser_Success() throws Exception {
		List<FavoriteResponse> favorites = new ArrayList<>();
		favorites.add(favoriteResponse);

		when(favoriteService.getDocumentFavoritesByUser()).thenReturn(favorites);

		mockMvc.perform(get("/api/favorites/document/user")).andExpect(status().isOk())
				.andExpect(jsonPath("$.resultList[0].contentId").value(1L));
	}

	@Test
	@DisplayName("GET /api/favorites/lesson/user - Should get lesson favorites by user")
	@WithMockUser(authorities = "GET_LESSON_FAVORITE")
	void testGetLessonFavoritesByUser_Success() throws Exception {
		FavoriteResponse lessonFavorite = FavoriteResponse.builder().id(1L).contentId(1L).title("Test Lesson")
				.thumbnailUrl("http://example.com/thumb.jpg").authorName("Test User").build();

		List<FavoriteResponse> favorites = new ArrayList<>();
		favorites.add(lessonFavorite);

		when(favoriteService.getLessonFavoritesByUser()).thenReturn(favorites);

		mockMvc.perform(get("/api/favorites/lesson/user")).andExpect(status().isOk())
				.andExpect(jsonPath("$.resultList[0].contentId").value(1L));
	}

	@Test
	@DisplayName("GET /api/favorites/document/user/check/{documentId} - Should check document favorite")
	@WithMockUser(authorities = "CHECK_DOCUMENT_FAVORITE")
	void testCheckDocumentFavorite_True() throws Exception {
		when(favoriteService.checkDocumentFavorite(anyLong())).thenReturn(true);

		mockMvc.perform(get("/api/favorites/document/user/check/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value(true));
	}

	@Test
	@DisplayName("GET /api/favorites/document/user/check/{documentId} - Should return false when not favorite")
	@WithMockUser(authorities = "CHECK_DOCUMENT_FAVORITE")
	void testCheckDocumentFavorite_False() throws Exception {
		when(favoriteService.checkDocumentFavorite(anyLong())).thenReturn(false);

		mockMvc.perform(get("/api/favorites/document/user/check/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value(false));
	}

	@Test
	@DisplayName("GET /api/favorites/lesson/user/check/{lessonId} - Should check lesson favorite")
	@WithMockUser(authorities = "CHECK_LESSON_FAVORITE")
	void testCheckLessonFavorite_True() throws Exception {
		when(favoriteService.checkLessonFavorite(anyLong())).thenReturn(true);

		mockMvc.perform(get("/api/favorites/lesson/user/check/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value(true));
	}

	@Test
	@DisplayName("GET /api/favorites/lesson/user/check/{lessonId} - Should return false when not favorite")
	@WithMockUser(authorities = "CHECK_LESSON_FAVORITE")
	void testCheckLessonFavorite_False() throws Exception {
		when(favoriteService.checkLessonFavorite(anyLong())).thenReturn(false);

		mockMvc.perform(get("/api/favorites/lesson/user/check/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value(false));
	}
}
