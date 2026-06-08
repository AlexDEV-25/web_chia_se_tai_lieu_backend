package com.example.app.dto.response.document;

import com.example.app.constant.ContentStatus;
import com.example.app.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentEventDTO {
	private Long id;
	private String title;
	private User user;
	private ContentStatus status;
}
