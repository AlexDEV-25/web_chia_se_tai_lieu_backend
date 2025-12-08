package com.example.app.dto.request;

import com.example.app.share.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequest {
	private String title;
	private String description;
	private Long viewsCount;
	private Long downloadsCount;
	private Status status;
	private boolean hide = false;
	private Long categoryId;
}
