package com.example.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.example.app.dto.request.CommentRequest;
import com.example.app.dto.response.comment.CommentResponse;
import com.example.app.dto.response.comment.CommentTreeResponse;
import com.example.app.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@DisplayName("CommentController Tests")
class CommentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CommentService commentService;

	@Autowired
	private ObjectMapper objectMapper;

	private CommentResponse commentResponse;
	private CommentTreeResponse commentTreeResponse;
	private CommentRequest commentRequest;

	@BeforeEach
	void setUp() {
		commentResponse = CommentResponse.builder().id(1L).content("Great content!").userId(1L).build();

		commentTreeResponse = CommentTreeResponse.builder().id(1L).content("Great content!").userId(1L).parentId(null)
				.build();

		commentRequest = CommentRequest.builder().content("Great content!").contentId(1L).type(InteractionType.DOCUMENT)
				.parentId(null).hide(false).build();
	}

	@Test
	@DisplayName("GET /api/comments/document/{docId} - Should get document comment tree")
	void testGetDocumentCommentTree_Success() throws Exception {
		List<CommentTreeResponse> comments = new ArrayList<>();
		comments.add(commentTreeResponse);

		when(commentService.getDocumentTree(anyLong())).thenReturn(comments);

		mockMvc.perform(get("/api/comments/document/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.resultList[0].content").value("Great content!"))
				.andExpect(jsonPath("$.resultList[0].userId").value(1L));
	}

	@Test
	@DisplayName("GET /api/comments/lesson/{lessonId} - Should get lesson comment tree")
	void testGetLessonCommentTree_Success() throws Exception {
		List<CommentTreeResponse> comments = new ArrayList<>();
		comments.add(commentTreeResponse);

		when(commentService.getLessonTree(anyLong())).thenReturn(comments);

		mockMvc.perform(get("/api/comments/lesson/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.resultList[0].content").value("Great content!"));
	}

	@Test
	@DisplayName("POST /api/comments - Should create comment successfully")
	@WithMockUser(authorities = "POST_COMMENT")
	void testCreateMyComment_Success() throws Exception {
		when(commentService.saveMyComment(any(CommentRequest.class))).thenReturn(commentResponse);

		mockMvc.perform(post("/api/comments").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(commentRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.id").value(1L))
				.andExpect(jsonPath("$.result.content").value("Great content!"));
	}

	@Test
	@DisplayName("PUT /api/comments/{id} - Should update comment successfully")
	@WithMockUser(authorities = "UPDATE_MY_COMMENT")
	void testUpdateMyComment_Success() throws Exception {
		CommentRequest updateRequest = CommentRequest.builder().content("Updated content!").contentId(1L).parentId(null)
				.type(InteractionType.DOCUMENT).build();

		CommentResponse updatedResponse = CommentResponse.builder().id(1L).content("Updated content!").userId(1L)
				.build();

		when(commentService.updateMyComment(anyLong(), any(CommentRequest.class))).thenReturn(updatedResponse);

		mockMvc.perform(put("/api/comments/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.result.content").value("Updated content!"));
	}

	@Test
	@DisplayName("GET /api/comments/document/{docId} - Should return empty list when no comments exist")
	void testGetDocumentCommentTree_Empty() throws Exception {
		List<CommentTreeResponse> comments = new ArrayList<>();

		when(commentService.getDocumentTree(anyLong())).thenReturn(comments);

		mockMvc.perform(get("/api/comments/document/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.resultList").isArray()).andExpect(jsonPath("$.resultList.length()").value(0));
	}
}
