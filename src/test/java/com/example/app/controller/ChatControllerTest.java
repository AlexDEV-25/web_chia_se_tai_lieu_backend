package com.example.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
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

import com.example.app.dto.response.ai.ChatHistoryResponse;
import com.example.app.service.ChatService;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@DisplayName("ChatController Tests")
class ChatControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ChatService chatService;

	private ChatHistoryResponse chatHistoryResponse;

	@BeforeEach
	void setUp() {
		chatHistoryResponse = ChatHistoryResponse.builder().role("assistant")
				.content("I can help you with various tasks").build();
	}

	@Test
	@DisplayName("POST /api/chats - Should send chat message successfully")
	@WithMockUser(authorities = "CHAT_GEMINI")
	void testChat_Success() throws Exception {
		String expectedResponse = "This is AI response";

		when(chatService.chat(any(), anyString())).thenReturn(expectedResponse);

		mockMvc.perform(post("/api/chats").contentType(MediaType.MULTIPART_FORM_DATA).param("message", "Hello"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.result").value("This is AI response"));
	}

	@Test
	@DisplayName("POST /api/chats - Should send chat message with image successfully")
	@WithMockUser(authorities = "CHAT_GEMINI")
	void testChat_WithImage_Success() throws Exception {
		String expectedResponse = "Analysis of image: ...";

		when(chatService.chat(any(), anyString())).thenReturn(expectedResponse);

		mockMvc.perform(
				post("/api/chats").contentType(MediaType.MULTIPART_FORM_DATA).param("message", "What is this image?"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.result").value("Analysis of image: ..."));
	}

	@Test
	@DisplayName("GET /api/chats - Should get chat history successfully")
	@WithMockUser(authorities = "HISTORY_CHAT_GEMINI")
	void testGetChatHistory_Success() throws Exception {
		List<ChatHistoryResponse> history = new ArrayList<>();
		history.add(chatHistoryResponse);

		when(chatService.getChatHistory()).thenReturn(history);

		mockMvc.perform(get("/api/chats")).andExpect(status().isOk())
				.andExpect(jsonPath("$.resultList[0].role").value("assistant"))
				.andExpect(jsonPath("$.resultList[0].content").value("I can help you with various tasks"));
	}

	@Test
	@DisplayName("GET /api/chats - Should return empty list when no chat history exists")
	@WithMockUser(authorities = "HISTORY_CHAT_GEMINI")
	void testGetChatHistory_Empty() throws Exception {
		List<ChatHistoryResponse> history = new ArrayList<>();

		when(chatService.getChatHistory()).thenReturn(history);

		mockMvc.perform(get("/api/chats")).andExpect(status().isOk()).andExpect(jsonPath("$.resultList").isArray())
				.andExpect(jsonPath("$.resultList.length()").value(0));
	}
}
