package com.example.app.dto.response;

import com.example.app.share.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentRatingSummaryResponse {
	private Long id;
	private String title;
	private Double average;
	private Long total;
	private Type type;
}
