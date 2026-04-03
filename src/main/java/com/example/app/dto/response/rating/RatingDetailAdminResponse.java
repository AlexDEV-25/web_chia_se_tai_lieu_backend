package com.example.app.dto.response.rating;

import com.example.app.share.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDetailAdminResponse {
	private Long contentId;
	private String title;
	private Long star1;
	private Long star2;
	private Long star3;
	private Long star4;
	private Long star5;
	private Type type;
}
