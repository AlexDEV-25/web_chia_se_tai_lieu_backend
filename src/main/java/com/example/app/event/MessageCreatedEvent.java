package com.example.app.event;

import com.example.app.dto.response.chatmessage.ChatMessageResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageCreatedEvent {
	private ChatMessageResponse message;
}
