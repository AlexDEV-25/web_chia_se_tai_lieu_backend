package com.example.app.dto.request;

import com.example.app.share.DocumentType;
import com.example.app.share.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequest {
	private String title;
	private DocumentType type;
	private String description;
	private Long viewsCount = 0L;
	private Long downloadsCount = 0L;
	private Status status;
	private boolean hide = false;
	private Long categoryId;
}
