package com.example.app.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.app.dto.response.chatmessage.ChatMessageResponse;
import com.example.app.service.ChatMessageService;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@DisplayName("ChatMessageController Tests")
class ChatMessageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ChatMessageService chatMessageService;

	private ChatMessageResponse chatMessageResponse;

	@BeforeEach
	void setUp() {
		chatMessageResponse = ChatMessageResponse.builder().id(1L).message("Hello!").userId(1L).userName("testuser")
				.conversationId(1L).me(true).userAvatar("http://example.com/avatar.jpg").build();
	}

	@Test
	@DisplayName("GET /api/chat-messages/my-conversation-messages/{conversationId} - Should get messages")
	@WithMockUser(authorities = "GET_MESSAGE")
	void testGetMyMessages_Success() throws Exception {
		List<ChatMessageResponse> messages = new ArrayList<>();
		messages.add(chatMessageResponse);

		when(chatMessageService.getMessages(anyLong())).thenReturn(messages);

		mockMvc.perform(get("/api/chat-messages/my-conversation-messages/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.resultList[0].message").value("Hello!"))
				.andExpect(jsonPath("$.resultList[0].userId").value(1L));
	}

	@Test
	@DisplayName("GET /api/chat-messages/my-conversation-messages/{conversationId} - Should return empty list")
	@WithMockUser(authorities = "GET_MESSAGE")
	void testGetMyMessages_Empty() throws Exception {
		List<ChatMessageResponse> messages = new ArrayList<>();

		when(chatMessageService.getMessages(anyLong())).thenReturn(messages);

		mockMvc.perform(get("/api/chat-messages/my-conversation-messages/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.resultList").isArray()).andExpect(jsonPath("$.resultList.length()").value(0));
	}

	@Test
	@DisplayName("GET /api/chat-messages/my-conversation-messages/{conversationId} - Should get multiple messages")
	@WithMockUser(authorities = "GET_MESSAGE")
	void testGetMyMessages_Multiple() throws Exception {
		ChatMessageResponse message2 = ChatMessageResponse.builder().id(2L).message("Hi there!").userId(2L)
				.userName("otheruser").conversationId(1L).me(false).userAvatar("http://example.com/avatar2.jpg")
				.build();

		List<ChatMessageResponse> messages = new ArrayList<>();
		messages.add(chatMessageResponse);
		messages.add(message2);

		when(chatMessageService.getMessages(anyLong())).thenReturn(messages);

		mockMvc.perform(get("/api/chat-messages/my-conversation-messages/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.resultList.length()").value(2))
				.andExpect(jsonPath("$.resultList[0].message").value("Hello!"))
				.andExpect(jsonPath("$.resultList[1].message").value("Hi there!"));
	}
}
