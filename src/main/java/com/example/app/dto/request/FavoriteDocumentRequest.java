package com.example.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDocumentRequest {

	@NotNull(message = "userId không được để trống")
	private Long userId;

	@NotNull(message = "documentId không được để trống")
	private Long documentId;
}
