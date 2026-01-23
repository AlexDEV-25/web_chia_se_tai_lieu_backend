package com.example.app.dto.response;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
	private InputStreamResource resource;
	private long length;
	private MediaType mediaType;
	private String fileName;

}
