package com.example.app.dto.request;

import com.example.app.share.Type;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {

	@NotNull(message = "rating không được để trống")
	@Min(value = 1, message = "rating tối thiểu là 1")
	@Max(value = 5, message = "rating tối đa là 5")
	private Integer rating;

	@NotNull(message = "contentId không được để trống")
	private Long contentId;

	@NotNull(message = "userId không được để trống")
	private Long userId;

	@NotNull(message = "Type không được null")
	private Type type;
}
