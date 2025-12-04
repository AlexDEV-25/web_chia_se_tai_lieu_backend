package com.example.app.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
	@NotBlank(message = "Nội dung không được để trống")
	@Size(min = 5, message = "Nội dung phải chứa ít nhất 5 ký tự")
	private String content;

	private LocalDateTime createdAt;
	private Long idParent;
	private LocalDateTime updatedAt;
	private boolean hide = false;

	@NotNull(message = "documentId là bắt buộc")
	@Positive(message = "documentId phải lớn hơn 0")
	private Long documentId;

	@NotNull(message = "userId là bắt buộc")
	@Positive(message = "userId phải lớn hơn 0")
	private Long userId;
}
