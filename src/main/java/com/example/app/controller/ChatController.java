package com.example.app.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.dto.request.ChatRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.service.ChatService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/chats")
@AllArgsConstructor
public class ChatController {
	private final ChatService chatService;

	@PostMapping
	String chat(@RequestBody ChatRequest request) {
		return chatService.chat(request);
	}

	@PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	APIResponse<String> chatWithImage(@RequestParam(value = "image", required = false) MultipartFile file,
			@RequestParam("message") String request) {
		String response = chatService.chatWithImage(file, request);
		APIResponse<String> apiResponse = new APIResponse<String>();
		apiResponse.setResult(response);
		apiResponse.setMessage("chat success");
		return apiResponse;
	}

}
