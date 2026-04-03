package com.example.app.dto.response.lesson;

import java.time.LocalDateTime;

import com.example.app.share.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonDetailResponse {
	private Long id;
	private String title;
	private String lessonUrl;
	private String documentUrl;
	private String subFileUrl;
	private String description;
	private String thumbnailUrl;
	private Long viewsCount = 0L;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Status status;
	private boolean hide;
	private Long categoryId;
	private String categoryName;
	private Long userId;
	private String userName;
}
