package com.example.app.dto.response.document;

import com.example.app.constant.ContentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentUserResponse {
	private Long id;
	private String title;
	private String description;
	private String thumbnailUrl;
	private Long viewsCount;
	private Long downloadsCount;
	private ContentStatus status;
}
