package com.example.app.dto.response.ai;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QaContent {
	private String answer;
	private List<Section> sections;
	private List<String> followUpQuestions;
}
