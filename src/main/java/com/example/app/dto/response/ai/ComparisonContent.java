package com.example.app.dto.response.ai;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComparisonContent {
	private String subjectA;
	private String subjectB;
	private List<CompareRow> rows;
}
