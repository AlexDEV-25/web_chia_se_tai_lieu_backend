package com.example.app.event;

import com.example.app.model.LessonComment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonCommentCreatedEvent {
	private LessonComment comment;
	private LessonComment parentComment;

}
