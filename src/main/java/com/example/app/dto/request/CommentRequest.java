package com.example.app.dto.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
	private String content;
	private LocalDateTime createdAt;
	private Long idParent;
	private LocalDateTime updatedAt;
	private boolean hide = false;
	private Long documentId;
	private Long userId;
}
