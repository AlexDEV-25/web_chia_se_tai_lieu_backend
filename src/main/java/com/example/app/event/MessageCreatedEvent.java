package com.example.app.event;

import com.example.app.model.ChatMessage;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageCreatedEvent {
	private final ChatMessage message;
}
