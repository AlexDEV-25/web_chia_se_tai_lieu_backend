package com.example.app.dto.response.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LectureResponse {
	private String title;
	private String category;
	private String author;
	private Double average;
	private Long total;
	private Long viewCount;
}
