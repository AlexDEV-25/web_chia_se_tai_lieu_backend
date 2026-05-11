package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.ConversationRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.conversation.ConversationResponse;
import com.example.app.service.ConversationService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/conversations")
@AllArgsConstructor
public class ConversationController {
	private final ConversationService conversationService;

	@PostMapping("/direct")
	public APIResponse<ConversationResponse> createDirectConversation(@RequestBody @Valid ConversationRequest dto) {
		ConversationResponse response = conversationService.createDirectConversation(dto);
		APIResponse<ConversationResponse> apiResponse = new APIResponse<ConversationResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@PostMapping("/group")
	public APIResponse<ConversationResponse> createGroupConversation(@RequestBody @Valid ConversationRequest dto) {
		ConversationResponse response = conversationService.createGroupConversation(dto);
		APIResponse<ConversationResponse> apiResponse = new APIResponse<ConversationResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@GetMapping("/my-conversations")
	public APIResponse<ConversationResponse> getMyConversation() {
		List<ConversationResponse> response = conversationService.getMyConversations();
		APIResponse<ConversationResponse> apiResponse = new APIResponse<ConversationResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}
}
