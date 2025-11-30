package com.example.app.dto.response;

import java.time.LocalDateTime;

import com.example.app.share.DocumentType;
import com.example.app.share.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
	private Long id;
	private String title;
	private String fileUrl;
	private DocumentType type;
	private String description;
	private String thumbnailUrl;
	private Long viewsCount = 0L;
	private Long downloadsCount = 0L;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Status status;
	private boolean hide;
	private Long categoryId;
	private String categoryName;
	private Long userId;
}
