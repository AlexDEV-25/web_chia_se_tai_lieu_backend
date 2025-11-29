package com.example.app.dto.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRequest {
	private LocalDateTime createdAt = LocalDateTime.now();
	private Long userId;
	private Long documentId;
}
