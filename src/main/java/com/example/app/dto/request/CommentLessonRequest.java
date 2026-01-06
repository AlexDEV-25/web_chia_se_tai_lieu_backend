package com.example.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentLessonRequest {

	@NotBlank(message = "content không được để trống")
	private String content;

	private Long idParent;

	private boolean hide;

	@NotNull(message = "LessonId không được để trống")
	private Long LessonId;

	@NotNull(message = "userId không được để trống")
	private Long userId;
}
