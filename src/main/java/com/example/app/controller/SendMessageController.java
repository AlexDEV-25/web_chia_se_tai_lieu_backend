package com.example.app.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.example.app.constant.AppError;
import com.example.app.dto.request.ChatMessageRequest;
import com.example.app.exception.AppException;
import com.example.app.service.ChatMessageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SendMessageController {
	private final ChatMessageService chatMessageService;

	@MessageMapping("/chat")
	public void sendMessage(@Payload @Valid ChatMessageRequest request) {
		try {
			chatMessageService.createMessage(request);
		} catch (Exception e) {
			throw AppException.builder().appError(AppError.MESSAGE_SEND_FAILED).build();
		}
	}
}
