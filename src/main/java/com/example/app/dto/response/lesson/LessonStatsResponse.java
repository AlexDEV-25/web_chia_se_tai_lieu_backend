package com.example.app.dto.response.lesson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonStatsResponse {
	private long totalLessons;
	private long totalViews;
}
