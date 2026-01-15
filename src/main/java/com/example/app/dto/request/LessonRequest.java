package com.example.app.dto.request;

import com.example.app.share.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonRequest {

	@NotBlank(message = "title không được để trống")
	private String title;

	private String description;

	@NotNull(message = "Status không được null")
	private Status status;

	private boolean hide;

	private Long categoryId;
}
