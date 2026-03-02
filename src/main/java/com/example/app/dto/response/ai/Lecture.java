package com.example.app.dto.response.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lecture {
	private String title;
	private String category;
	private String author;
}
