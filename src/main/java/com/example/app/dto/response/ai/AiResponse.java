package com.example.app.dto.response.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiResponse<T> {
	private String type; // QA | STUDY_PLAN | SUMMARY | COMPARISON
	private String status; // SUCCESS | OUT_OF_SCOPE | ERROR
	private T data;
}
