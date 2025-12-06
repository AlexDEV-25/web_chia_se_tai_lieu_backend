package com.example.app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
	private String content;
	private Long idParent;
	private boolean hide = false;
	private Long documentId;
	private Long userId;
}
