package com.example.app.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OutboudUserResponse {
	private String id;
	private String email;
	private boolean verifiedEmail;
	private String name;
	private String givenName;
	private String familyName;
	private String picture;
	private String locale;
}
