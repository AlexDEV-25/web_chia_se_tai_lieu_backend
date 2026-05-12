package com.example.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
		ConversationResponse response = conversationService.createDirectConversation(dto);
		APIResponse<ConversationResponse> apiResponse = new APIResponse<ConversationResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@PostMapping(value = "/group", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public APIResponse<ConversationResponse> createGroupConversation(
			@RequestPart(value = "avt", required = false) MultipartFile avt, @RequestPart("data") String dataJson) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			ConversationGroupRequest dto = mapper.readValue(dataJson, ConversationGroupRequest.class);
			ConversationResponse response = conversationService.createGroupConversation(avt, dto);
			APIResponse<ConversationResponse> apiResponse = new APIResponse<ConversationResponse>();
			apiResponse.setResult(response);
			apiResponse.setMessage("save success");
			return apiResponse;
		} catch (Exception e) {
			throw new AppException("Cập nhật thất bại", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/my-conversations")
	public APIResponse<ConversationResponse> getMyConversation() {
		List<ConversationResponse> response = conversationService.getMyConversations();
		APIResponse<ConversationResponse> apiResponse = new APIResponse<ConversationResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/search")
	public APIResponse<ConversationResponse> search(@RequestParam String keyword) {
		List<ConversationResponse> response = conversationService.search(keyword);
		APIResponse<ConversationResponse> apiResponse = new APIResponse<ConversationResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}
}
