package com.example.app.dto.response.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
	private Long id;
	private String title;
	private String description;
	private String thumbnailUrl;
	private String username;
	private Long viewsCount;
	private Long downloadsCount;
	private boolean favorite;
}
