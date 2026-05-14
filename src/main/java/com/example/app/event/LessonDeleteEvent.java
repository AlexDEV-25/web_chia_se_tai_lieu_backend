package com.example.app.event;

import com.example.app.model.Lesson;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonDeleteEvent {
	private Lesson lesson;
}
