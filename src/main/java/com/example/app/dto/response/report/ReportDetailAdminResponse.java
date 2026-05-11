package com.example.app.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDetailAdminResponse {
	private Long id;
	private Long contentId;
	private String title;
	private String username;
	private String reason;
}
