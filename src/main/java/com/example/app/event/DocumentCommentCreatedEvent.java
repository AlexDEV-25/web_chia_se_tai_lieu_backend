package com.example.app.event;

import com.example.app.model.DocumentComment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentCommentCreatedEvent {
	private final DocumentComment comment;
	private final DocumentComment parentComment;
}
