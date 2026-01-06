package com.example.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDocumentRequest {

	@NotBlank(message = "content không được để trống")
	private String content;

	private Long idParent;

	private boolean hide;

	@NotNull(message = "documentId không được để trống")
	private Long documentId;

	@NotNull(message = "userId không được để trống")
	private Long userId;
}
