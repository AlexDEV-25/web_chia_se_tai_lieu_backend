package com.example.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import com.example.app.dto.request.ChatMessageRequest;
import com.example.app.dto.response.chatmessage.ChatMessageResponse;
import com.example.app.service.ChatMessageService;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@DisplayName("SendMessageController Tests")
public class SendMessageControllerTest {
	@Autowired
	private SendMessageController sendMessageController;

	@MockBean
	private ChatMessageService chatMessageService;

	private ChatMessageResponse chatMessageResponse;

	private ChatMessageRequest chatMessageRequest;

	@BeforeEach
	void setUp() {
		chatMessageResponse = ChatMessageResponse.builder().id(1L).message("Hello!").userId(1L).userName("testuser")
				.conversationId(1L).me(true).userAvatar("http://example.com/avatar.jpg").build();

		chatMessageRequest = ChatMessageRequest.builder().conversationId(1L).message("Hello!").build();
	}

	@Test
	@DisplayName("GET /api/chat - Should chat messages")
	@WithMockUser(authorities = "CREATE_MESSAGE")
	void testChat_Success() throws Exception {

		when(chatMessageService.createMessage(any(ChatMessageRequest.class))).thenReturn(chatMessageResponse);

		sendMessageController.sendMessage(chatMessageRequest);

		// verify service được gọi
		verify(chatMessageService).createMessage(chatMessageRequest);
	}
}
