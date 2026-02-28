package com.example.app.dto.response.ai;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompareRow {
	private String aspect; // Tiêu chí

	// key = subject name
	// value = description
	private Map<String, String> values;
}
