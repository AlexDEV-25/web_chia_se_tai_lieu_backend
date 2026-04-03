package com.example.app.dto.response.report;

import com.example.app.share.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportAdminResponse {
	private Long id;
	private String title;
	private Long total;
	private Type type;
}
