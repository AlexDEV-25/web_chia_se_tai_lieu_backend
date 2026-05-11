package com.example.app.dto.response.lesson;

import com.example.app.constant.ContentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonAdminResponse {
	private Long id;
	private String title;
	private String description;
	private String categoryName;
	private ContentStatus status;
}
