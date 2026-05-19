package com.example.app.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.constant.AppError;
import com.example.app.dto.request.ConversationGroupRequest;
import com.example.app.dto.request.ConversationRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.conversation.ConversationResponse;
import com.example.app.exception.AppException;
import com.example.app.service.ConversationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/conversations")
@AllArgsConstructor
public class ConversationController {
	private final ConversationService conversationService;

	@PostMapping("/direct")
	public APIResponse<ConversationResponse> createDirectConversation(@RequestBody @Valid ConversationRequest dto) {
		APIResponse<ConversationResponse> apiResponse = new APIResponse<ConversationResponse>();
		apiResponse.setResult(conversationService.createDirectConversation(dto));
		return apiResponse;
	}

	@PostMapping(value = "/group", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public APIResponse<ConversationResponse> createGroupConversation(
			@RequestPart(value = "avt", required = false) MultipartFile avt, @RequestPart("data") String dataJson) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			ConversationGroupRequest dto = mapper.readValue(dataJson, ConversationGroupRequest.class);

			APIResponse<ConversationResponse> apiResponse = new APIResponse<ConversationResponse>();
			apiResponse.setResult(conversationService.createGroupConversation(avt, dto));
			return apiResponse;

		} catch (Exception e) {
			throw AppException.builder().appError(AppError.INVALID_JSON_FORMAT).build();
		}
	}

	@GetMapping("/my-conversations")
	public APIResponse<ConversationResponse> getMyConversation() {
		APIResponse<ConversationResponse> apiResponse = new APIResponse<ConversationResponse>();
		apiResponse.setResultList(conversationService.getMyConversations());
		return apiResponse;
	}

	@GetMapping("/search")
	public APIResponse<ConversationResponse> search(@RequestParam String keyword) {
		APIResponse<ConversationResponse> apiResponse = new APIResponse<ConversationResponse>();
		apiResponse.setResultList(conversationService.search(keyword));
		return apiResponse;
	}

	@GetMapping("/detail/{id}")
	public APIResponse<ConversationResponse> getDetailConversation(@PathVariable Long id) {
		APIResponse<ConversationResponse> apiResponse = new APIResponse<ConversationResponse>();
		apiResponse.setResult(conversationService.getDetailConversation(id));
		return apiResponse;
	}
}
