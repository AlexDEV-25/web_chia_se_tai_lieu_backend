package com.example.app.event;

import com.example.app.constant.NotificationAction;
import com.example.app.dto.response.document.DocumentEventDTO;
import com.example.app.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentStatusEvent {
	private final DocumentEventDTO document;
	private final User sender;
	private final NotificationAction action;
}
