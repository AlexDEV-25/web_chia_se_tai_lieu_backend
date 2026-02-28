package com.example.app.dto.response.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiResponse {
	private String type; // QA | STUDY_PLAN | COMPARISON
	private String status; // SUCCESS | OUT_OF_SCOPE | ERROR
	private ComparisonContent comparisonContent;
	private QaContent qaContent;
	private StudyPlanContent studyPlanContent;
	private String conclusion;

}
