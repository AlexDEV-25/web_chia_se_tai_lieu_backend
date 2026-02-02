package com.example.app.dto.response;

import com.example.app.share.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
	private Long id;
	private String reason;
	private Type type;
}
