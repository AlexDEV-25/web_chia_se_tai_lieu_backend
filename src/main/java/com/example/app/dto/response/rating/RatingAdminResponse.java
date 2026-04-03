package com.example.app.dto.response.rating;

import com.example.app.share.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingAdminResponse {
	private Long id;
	private String title;
	private Double average;
	private Long total;
	private Type type;
}
