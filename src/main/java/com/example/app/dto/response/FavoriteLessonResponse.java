package com.example.app.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteLessonResponse {
	private Long id;
	private LocalDateTime createdAt;
	private Long userId;
	private Long lessonId;
	private String lessonTitle;
	private String lessonThumbnailUrl;
	private String authorName;
}
