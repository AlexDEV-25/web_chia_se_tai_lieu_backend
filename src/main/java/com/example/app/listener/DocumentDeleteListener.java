package com.example.app.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.app.event.DocumentDeleteEvent;
import com.example.app.helper.FileManager;
import com.example.app.model.Document;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DocumentDeleteListener {
	private final FileManager fileManager;

	@EventListener
	public void handle(DocumentDeleteEvent event) {
		Document entity = event.getDocument();
		try {
			fileManager.deleteFile(entity.getFileUrl(), "image");
			fileManager.deleteFile(entity.getThumbnailUrl(), "image");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
