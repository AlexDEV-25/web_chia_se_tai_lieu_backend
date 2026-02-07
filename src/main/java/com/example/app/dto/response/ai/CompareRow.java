package com.example.app.dto.response.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompareRow {
	private String aspect; // Tiêu chí so sánh
	private String valueA; // Giá trị của subjectA
	private String valueB; // Giá trị của subjectB
}
