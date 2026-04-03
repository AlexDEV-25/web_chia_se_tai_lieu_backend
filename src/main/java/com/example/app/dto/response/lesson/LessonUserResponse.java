package com.example.app.dto.response.lesson;

import com.example.app.share.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonUserResponse {
	private Long id;
	private String title;
	private String description;
	private String thumbnailUrl;
	private Long viewsCount = 0L;
	private boolean hide;
	private Long categoryId;
	private Status status;
}
