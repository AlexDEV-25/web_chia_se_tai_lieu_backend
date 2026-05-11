package com.example.app.event;

import com.example.app.constant.NotificationAction;
import com.example.app.dto.response.lesson.LessonDTO;
import com.example.app.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonStatusEvent {
	private final LessonDTO lesson;
	private final User sender;
	private final NotificationAction action;
}
