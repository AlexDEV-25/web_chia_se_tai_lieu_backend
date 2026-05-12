package com.example.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ConversationGroupRequest extends ConversationRequest {
	@NotBlank(message = "Tên nhóm không được để trống")
	private String groupName;
}
