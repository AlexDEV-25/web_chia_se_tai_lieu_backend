package com.example.app.dto.request;

import com.example.app.share.Type;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {

	@NotNull(message = "contentId không được để trống")
	private Long contentId;

	@NotBlank(message = "lý do không được để trống")
	private String reason;

	@NotNull(message = "Type không được null")
	private Type type;
}
