package com.example.app.dto.response.lesson;

import java.time.LocalDateTime;

import com.example.app.constant.ContentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonDetailResponse {
	private Long id;
	private String title;
	private String lessonUrl;
	private String documentUrl;
	private String subFileUrl;
	private String description;
	private String thumbnailUrl;
	private Long viewsCount;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private ContentStatus status;
	private boolean hide;
	private Long categoryId;
	private String categoryName;
	private Long userId;
	private String userName;
}
