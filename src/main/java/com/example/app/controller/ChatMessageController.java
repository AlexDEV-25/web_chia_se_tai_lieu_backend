package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.chatmessage.ChatMessageResponse;
import com.example.app.service.ChatMessageService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/chat-messages")
@AllArgsConstructor
public class ChatMessageController {
	private final ChatMessageService chatMessageService;

	@GetMapping("/my-conversation-messages/{conversationId}")
	public APIResponse<ChatMessageResponse> getMyMessages(@PathVariable Long conversationId) {
		List<ChatMessageResponse> response = chatMessageService.getMessages(conversationId);
		APIResponse<ChatMessageResponse> apiResponse = new APIResponse<ChatMessageResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}
}
