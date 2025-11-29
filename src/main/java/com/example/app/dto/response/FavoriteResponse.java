package com.example.app.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {
	private Long id;
	private LocalDateTime createdAt;
	private Long userId;
	private Long documentId;
	private String documentTitle;
	private String documentThumbnailUrl;
}
