package com.example.app.dto.request;

import com.example.app.constant.InteractionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

	@NotBlank(message = "content không được để trống")
	private String content;

	private Long parentId;

	private boolean hide;

	@NotNull(message = "contentId không được để trống")
	private Long contentId;

	@NotNull(message = "Type không được null")
	private InteractionType type;
}
