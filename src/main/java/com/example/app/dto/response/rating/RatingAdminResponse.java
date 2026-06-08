package com.example.app.dto.response.rating;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingAdminResponse {
	private Long id;
	private String title;
	private Double average;
	private Long total;
}
