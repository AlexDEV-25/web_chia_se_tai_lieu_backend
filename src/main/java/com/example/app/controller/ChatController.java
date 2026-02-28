package com.example.app.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.ChatHistoryResponse;
import com.example.app.dto.response.CommentResponse;
import com.example.app.dto.response.ai.AiResponse;
import com.example.app.service.ChatService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/chats")
@AllArgsConstructor
public class ChatController {
	private final ChatService chatService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	APIResponse<String> chat(@RequestParam(value = "image", required = false) MultipartFile file,
			@RequestParam("message") String request) {
		String response = chatService.chat(file, request);
		APIResponse<String> apiResponse = new APIResponse<String>();
		apiResponse.setResult(response);
		apiResponse.setMessage("chat success");
		return apiResponse;
	}

	@PostMapping(value = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	APIResponse<AiResponse> newChat(@RequestParam(value = "image", required = false) MultipartFile file,
			@RequestParam("message") String request) {
		AiResponse response = chatService.newChat(file, request);
		APIResponse<AiResponse> apiResponse = new APIResponse<AiResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("chat success");
		return apiResponse;
	}

	@PostMapping("/test")
	APIResponse<String> test(@RequestParam("message") String request) {
		String response = chatService.test(request);
		APIResponse<String> apiResponse = new APIResponse<String>();
		apiResponse.setResult(response);
		apiResponse.setMessage("chat success");
		return apiResponse;
	}

	@GetMapping("/admin/filter-comment")
	APIResponse<CommentResponse> filterCommnent() {
		List<CommentResponse> response = chatService.filterCommnent();
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("chat success");
		return apiResponse;
	}

	@GetMapping()
	public APIResponse<ChatHistoryResponse> getChatHistory() {
		List<ChatHistoryResponse> response = chatService.getChatHistory();
		APIResponse<ChatHistoryResponse> apiResponse = new APIResponse<ChatHistoryResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}
}
