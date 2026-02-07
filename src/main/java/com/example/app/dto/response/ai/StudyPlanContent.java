package com.example.app.dto.response.ai;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyPlanContent {
	private String goal;
	private int totalDays;
	private List<StudyDay> schedule;
}
