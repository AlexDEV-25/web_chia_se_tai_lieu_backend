package com.example.app.dto.response.document;

import java.time.LocalDateTime;

import com.example.app.constant.ContentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDetailResponse {
	private Long id;
	private String title;
	private String description;
	private String fileUrl;
	private String thumbnailUrl;
	private Long viewsCount;
	private Long downloadsCount;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private ContentStatus status;
	private boolean hide;
	private Long categoryId;
	private String categoryName;
	private Long userId;
	private String userName;
}
