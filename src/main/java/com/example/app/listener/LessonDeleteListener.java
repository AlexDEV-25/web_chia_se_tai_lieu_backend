package com.example.app.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.app.event.LessonDeleteEvent;
import com.example.app.helper.FileManager;
import com.example.app.model.Lesson;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LessonDeleteListener {
	private final FileManager fileManager;

	@EventListener
	public void handle(LessonDeleteEvent event) {
		Lesson entity = event.getLesson();
		try {
			fileManager.deleteFile(entity.getLessonUrl(), "video");
			fileManager.deleteFile(entity.getDocumentUrl(), "image");
			fileManager.deleteFile(entity.getThumbnailUrl(), "image");
			fileManager.deleteFile(entity.getSubFileUrl(), "raw");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
