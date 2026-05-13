package com.example.app.listener;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.example.app.event.MessageCreatedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageCreatedListener {
	private final SimpMessagingTemplate messagingTemplate;

	@EventListener
	public void handleMessageCreated(MessageCreatedEvent event) {

		messagingTemplate.convertAndSend("/topic/conversation/" + event.getMessage().getConversationId(),
				event.getMessage());
	}
}
