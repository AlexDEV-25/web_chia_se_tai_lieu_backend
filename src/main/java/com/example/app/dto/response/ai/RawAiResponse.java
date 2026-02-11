package com.example.app.dto.response.ai;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawAiResponse {
	private String type;
	private String status;
	private JsonNode data; // quan trọng
}
