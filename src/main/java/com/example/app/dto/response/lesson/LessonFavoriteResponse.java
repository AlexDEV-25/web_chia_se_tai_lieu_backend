package com.example.app.dto.response.lesson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonFavoriteResponse {
	private Long id;
	private String title;
	private String description;
	private String thumbnailUrl;
	private String username;
	private Long viewsCount = 0L;
	private boolean favorite;
}
