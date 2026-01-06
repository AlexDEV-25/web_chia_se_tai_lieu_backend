package com.example.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteLessonRequest {

	@NotNull(message = "userId không được để trống")
	private Long userId;

	@NotNull(message = "LessonId không được để trống")
	private Long LessonId;
}
